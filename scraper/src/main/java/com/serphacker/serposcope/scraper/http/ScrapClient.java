/* 
 * Serposcope - SEO rank checker https://serposcope.serphacker.com/
 * 
 * Copyright (c) 2016 SERP Hacker
 * @author Pierre Nogues <support@serphacker.com>
 * @license https://opensource.org/licenses/MIT MIT License
 */
package com.serphacker.serposcope.scraper.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serphacker.serposcope.scraper.http.extensions.CloseableBasicHttpClientConnectionManager;
import com.serphacker.serposcope.scraper.http.extensions.ScrapClientPlainConnectionFactory;
import com.serphacker.serposcope.scraper.http.extensions.ScrapClientSSLConnectionFactory;
import com.serphacker.serposcope.scraper.http.extensions.ScrapClientSocksAuthenticator;
import com.serphacker.serposcope.scraper.http.factory.GetRequestFactory;
import com.serphacker.serposcope.scraper.http.factory.HttpRequestFactory;
import com.serphacker.serposcope.scraper.http.factory.PostRequestFactory;
import com.serphacker.serposcope.scraper.http.proxy.DirectNoProxy;
import com.serphacker.serposcope.scraper.http.proxy.HttpProxy;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.serphacker.serposcope.scraper.http.proxy.ScrapProxy;
import com.serphacker.serposcope.scraper.http.proxy.SocksProxy;
import com.serphacker.serposcope.scraper.utils.EncodeUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.client.RedirectLocations;
import org.apache.http.entity.ContentType;

public class ScrapClient implements Closeable, CredentialsProvider {

    private static final Logger LOG = LoggerFactory.getLogger(ScrapClient.class);

    private final static ObjectMapper jsonMapper = new ObjectMapper();

    public final static String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0";
    public final static int DEFAULT_TIMEOUT_MS = 30000;
    public final static int DEFAULT_MAX_RESPONSE_LENGTH = (1024 * 1024 * 4) - 1;// 4MB

    CloseableHttpClient client;
    BasicCredentialsProvider credentialProvider = new BasicCredentialsProvider();
    BasicCookieStore basicCookieStore = new BasicCookieStore();
    final CloseableBasicHttpClientConnectionManager connManager;
    ScrapClientPlainConnectionFactory plainConnectionFactory = new ScrapClientPlainConnectionFactory();
    ScrapClientSSLConnectionFactory sslConnectionFactory = new ScrapClientSSLConnectionFactory(plainConnectionFactory);

    String useragent = DEFAULT_USER_AGENT;
    Integer timeoutMS = DEFAULT_TIMEOUT_MS;
    ScrapProxy proxy;
    int maxResponseLength;
    byte[] buffer;
    List<Header> requestHeaders = new ArrayList<>();
    Map<HttpHost, HttpHost> routes = new HashMap<>();
    boolean proxyChangedSinceLastRequest;
    int maxRedirect = 0;

    long executionTimeMS;
    CloseableHttpResponse response;
    byte[] content;
    int statusCode;
    Exception exception;
    String lastRedirect;

    public ScrapClient() {
        this(ScrapClientConfig.builder().build());
    }

    public ScrapClient(ScrapClientConfig config) {
        applyMaxResponseLength(config.getMaxResponseLength());

        sslConnectionFactory.setInsecure(config.isInsecureSSL());

        connManager = new CloseableBasicHttpClientConnectionManager(
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", plainConnectionFactory)
                        .register("https", sslConnectionFactory)
                        .build());

        client = HttpClients
                .custom()
                .setRoutePlanner(new ScrapClientRoutePlanner(routes, () -> proxy))
                .setDefaultCredentialsProvider(this)
                .setDefaultCookieStore(basicCookieStore)
                .setConnectionReuseStrategy(
                        new ScrapClientConnectionReuseStrategy(() -> proxyChangedSinceLastRequest, () -> proxy))
                .setConnectionManager(connManager)
                .build();

        // Aplicar configurações do config
        this.useragent = config.getUserAgent();
        setTimeout(config.getTimeoutMS());
        if (config.getProxy() != null) {
            setProxy(config.getProxy());
        }
        this.maxRedirect = config.getMaxRedirect();
        this.requestHeaders.addAll(config.getRequestHeaders());
    }

    public void addCookie(Cookie cookie) {
        basicCookieStore.addCookie(cookie);
    }

    public void addCookies(Cookie[] cookies) {
        basicCookieStore.addCookies(cookies);
    }

    public void addCookies(Collection<Cookie> cookies) {
        for (Cookie cooky : cookies) {
            basicCookieStore.addCookie(cooky);
        }
    }

    public List<Cookie> getCookies() {
        return basicCookieStore.getCookies();
    }

    public boolean clearExpiredCookies(Date date) {
        return basicCookieStore.clearExpired(date);
    }

    public void clearCookies() {
        basicCookieStore.clear();
    }

    public String getUseragent() {
        return useragent;
    }

    public void setUserAgent(String userAgent) {
        this.useragent = userAgent;
    }

    public void setProxy(ScrapProxy proxy) {
        synchronized (connManager) {
            connManager.closeConnection();
        }
        proxyChangedSinceLastRequest = true;
        if (proxy != null && proxy instanceof DirectNoProxy) {
            this.proxy = null;
        } else {
            this.proxy = proxy;
        }

        if (proxy instanceof SocksProxy) {
            ScrapClientSocksAuthenticator.INSTANCE.addProxy((SocksProxy) proxy);
        }
    }

    public ScrapProxy getProxy() {
        return proxy;
    }

    public Integer getTimeout() {
        return timeoutMS;
    }

    public final void setTimeout(Integer timeoutMS) {
        this.timeoutMS = timeoutMS;
        SocketConfig.Builder newSocketConfig = SocketConfig.custom();
        if (timeoutMS != null) {
            newSocketConfig.setSoTimeout(timeoutMS);
        }
        connManager.setSocketConfig(newSocketConfig.build());
    }

    public int getMaxResponseLength() {
        return maxResponseLength;
    }

    public int getMaxRedirect() {
        return maxRedirect;
    }

    public boolean isInsecureSSL() {
        return sslConnectionFactory.isInsecure();
    }

    private void applyMaxResponseLength(int maxResponseLength) {
        this.maxResponseLength = maxResponseLength + 1;
        buffer = new byte[this.maxResponseLength];
    }

    public CloseableHttpResponse getResponse() {
        return response;
    }

    public byte[] getContent() {
        return content;
    }

    public String getContentAsString() {
        if (response == null || content == null) {
            return null;
        }

        Charset charset = getDetectedCharset();

        if (charset == null) {
            charset = Charset.forName("UTF-8");
        }

        return new String(content, charset);
    }

    public Charset getDetectedCharset() {
        ContentType contentType = null;
        try {
            contentType = ContentType.get(response.getEntity());
        } catch (Exception ex) {
        }

        Charset charset = null;
        if (contentType != null) {
            try {
                charset = contentType.getCharset();
            } catch (final Exception ex) {
            }

            if (charset == null) {
                if (contentType.getMimeType().contains("text/html")) {
                    charset = detectCharsetFromHtmlMeta();
                }
            }

        }

        return charset;
    }

    final static Pattern pcharset = Pattern.compile("charset=['\"]?([^\"'\\s]+)");

    protected Charset detectCharsetFromHtmlMeta() {
        if (content == null) {
            return null;
        }

        int len = content.length > 4096 ? 4096 : content.length;
        Matcher matcher = pcharset.matcher(new ByteCharSequence(content, 0, len));
        if (matcher.find()) {
            try {
                return Charset.forName(matcher.group(1));
            } catch (Exception ex) {
            }
        }

        return null;
    }

    public String getResponseHeader(String key) {
        if (response == null) {
            return null;
        }
        Header header = response.getFirstHeader(key);
        if (header == null) {
            return null;
        }
        return header.getValue();
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Exception getException() {
        return exception;
    }

    public int get(String url) {
        return get(url, null);
    }

    public int get(String url, String referrer) {
        return execute(new GetRequestFactory(url, useragent, referrer));
    }

    private int execute(HttpRequestFactory factory) {
        return request(factory.create());
    }

    public int post(String url, Map<String, Object> data, PostType dataType) {
        return post(url, data, dataType, null);
    }

    public int post(String url, Map<String, Object> data, PostType dataType, String charset) {
        return post(url, data, dataType, charset, null);
    }

    public int post(String url, Map<String, Object> data, PostType dataType, String charset, String referrer) {
        clearPreviousRequest();

        Charset resolvedCharset = resolveCharset(charset);
        Map<String, Object> sanitizedData = sanitizePostData(data, resolvedCharset);

        try {
            HttpEntity entity = dataType.createEntity(sanitizedData, resolvedCharset, jsonMapper);
            return execute(new PostRequestFactory(url, useragent, entity, referrer));
        } catch (Exception ex) {
            statusCode = -1;
            exception = ex;
            return statusCode;
        }
    }

    private Charset resolveCharset(String charsetName) {
        if (charsetName == null || charsetName.isEmpty()) {
            return StandardCharsets.UTF_8;
        }

        try {
            return Charset.forName(charsetName);
        } catch (Exception ex) {
            LOG.warn("invalid charset name {}, switching to utf-8", charsetName);
            return StandardCharsets.UTF_8;
        }
    }

    private Map<String, Object> sanitizePostData(Map<String, Object> originalData, Charset charset) {
        if (originalData == null || originalData.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Object> cleanedData = new HashMap<>();
        boolean hasUnsupportedEncoding = false;

        for (Map.Entry<String, Object> entry : originalData.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (!EncodeUtils.canEncode(key, charset.name())) {
                hasUnsupportedEncoding = true;
                key = EncodeUtils.forceASCII(key);
            }

            if (value instanceof String) {
                if (!EncodeUtils.canEncode((String) value, charset.name())) {
                    hasUnsupportedEncoding = true;
                    value = EncodeUtils.forceASCII((String) value);
                }
            }

            cleanedData.put(key, value);
        }

        if (hasUnsupportedEncoding) {
            LOG.warn("failed to encode some post data to {} forced to ascii", charset.name());
        }

        return cleanedData;
    }

    protected void clearPreviousRequest() {
        content = null;
        exception = null;
        response = null;
        statusCode = 0;
        lastRedirect = null;
    }

    public int request(HttpRequestBase request) {
        synchronized (connManager) {
            try {
                clearPreviousRequest();
                executionTimeMS = System.currentTimeMillis();

                HttpClientContext context = HttpClientContext.create();
                initializeRequest(request, context);

                response = client.execute(request, context);
                statusCode = response.getStatusLine().getStatusCode();
                RedirectLocations redirects = context.getAttribute(HttpClientContext.REDIRECT_LOCATIONS,
                        RedirectLocations.class);
                if (redirects != null && !redirects.isEmpty()) {
                    lastRedirect = redirects.get(redirects.size() - 1).toString();
                }

                HttpEntity entity = response.getEntity();
                long contentLength = entity.getContentLength();

                if (contentLength > maxResponseLength) {
                    throw new ResponseTooBigException(
                            "content length (" + contentLength + ") "
                                    + "is greater than max response leength (" + maxResponseLength + ")");
                }

                InputStream stream = entity.getContent();
                int totalRead = 0;
                int read = 0;

                while (totalRead < maxResponseLength
                        && (read = stream.read(buffer, totalRead, maxResponseLength - totalRead)) != -1) {
                    totalRead += read;
                }

                if (totalRead == maxResponseLength && read != 0) {
                    throw new ResponseTooBigException("already read " + totalRead + " bytes");
                }
                content = Arrays.copyOfRange(buffer, 0, totalRead);

            } catch (Exception ex) {
                content = null;
                statusCode = -1;
                exception = ex;
            } finally {
                proxyChangedSinceLastRequest = false;
                closeResponse();
                executionTimeMS = System.currentTimeMillis() - executionTimeMS;
            }

            return statusCode;
        }
    }

    protected void initializeRequest(HttpRequestBase request, HttpClientContext context) {
        if (request.getFirstHeader("user-agent") == null) {
            request.setHeader("User-Agent", useragent);
        }

        for (Header requestHeader : requestHeaders) {
            request.setHeader(requestHeader);
        }

        RequestConfig.Builder configBuilder = RequestConfig
                .copy(request.getConfig() == null ? RequestConfig.DEFAULT : request.getConfig());

        if (timeoutMS != null) {
            configBuilder.setConnectTimeout(timeoutMS);
            configBuilder.setConnectionRequestTimeout(timeoutMS);
            configBuilder.setSocketTimeout(timeoutMS);
        }

        if (maxRedirect == 0) {
            configBuilder.setRedirectsEnabled(false);
        } else {
            configBuilder.setMaxRedirects(maxRedirect);
        }

        RequestConfig config = configBuilder.build();

        context.setAttribute(HttpClientContext.REQUEST_CONFIG, config);
        request.setConfig(config);
    }

    public void closeResponse() {
        if (response != null) {
            try {
                response.close();
            } catch (Exception ex) {
                LOG.warn("Exception while closing response", ex);
            }
        }
    }

    @Override
    public void close() throws IOException {
        closeResponse();
        if (client != null) {
            client.close();
        }
    }

    public void setRoute(HttpHost to, HttpHost via) {
        routes.put(to, via);
    }

    public void removeRouteVia(HttpHost host) {
        routes.remove(host);
    }

    public void removeRoutesTo(String host) {
        routes.entrySet().removeIf((Map.Entry<HttpHost, HttpHost> t) -> host.equals(t.getValue().getHostName()));
    }

    public void removeRoutes() {
        routes.clear();
    }

    @Override
    public Credentials getCredentials(AuthScope authscope) {
        if (proxy != null && proxy instanceof HttpProxy) {
            HttpProxy httpProxy = (HttpProxy) proxy;
            if (httpProxy.getIp().equals(authscope.getHost())
                    && httpProxy.getPort() == authscope.getPort()
                    && httpProxy.getUsername() != null
                    && httpProxy.getPassword() != null) {
                return new UsernamePasswordCredentials(httpProxy.getUsername(), httpProxy.getPassword());
            }
        }

        return credentialProvider.getCredentials(authscope);
    }

    @Override
    public void setCredentials(AuthScope scope, Credentials auth) {
        credentialProvider.setCredentials(scope, auth);
    }

    @Override
    public void clear() {
        credentialProvider.clear();
    }

    public void setRequestHeader(Header header) {
        removeRequestHeadersByName(header.getName());
        requestHeaders.add(header);
    }

    public void removeRequestHeadersByName(String name) {
        requestHeaders.removeIf((Header t) -> t.getName().toLowerCase().equals(name.toLowerCase()));
    }

    public long getExecutionTimeMS() {
        return executionTimeMS;
    }

    public String getLastRedirect() {
        return lastRedirect;
    }

}
