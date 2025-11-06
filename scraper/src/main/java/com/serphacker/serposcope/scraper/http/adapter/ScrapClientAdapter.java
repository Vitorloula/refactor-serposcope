package com.serphacker.serposcope.scraper.http.adapter;

import com.serphacker.serposcope.scraper.http.ScrapClient;
import com.serphacker.serposcope.scraper.http.ScraperHttpClient;
import com.serphacker.serposcope.scraper.http.proxy.ScrapProxy;
import java.io.IOException;
import java.util.Map;
import org.apache.http.HttpHost;
import org.apache.http.cookie.Cookie;

/**
 * Adapter that exposes {@link ScrapClient} through the {@link ScraperHttpClient} interface.
 */
public class ScrapClientAdapter implements ScraperHttpClient {

    private final ScrapClient delegate;

    public ScrapClientAdapter(ScrapClient delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate must not be null");
        }
        this.delegate = delegate;
    }

    public ScrapClient getDelegate() {
        return delegate;
    }

    @Override
    public ScrapProxy getProxy() {
        return delegate.getProxy();
    }

    @Override
    public void setProxy(ScrapProxy proxy) {
        delegate.setProxy(proxy);
    }

    @Override
    public void setUseragent(String useragent) {
        delegate.setUseragent(useragent);
    }

    @Override
    public void removeRoutes() {
        delegate.removeRoutes();
    }

    @Override
    public void setRoute(HttpHost to, HttpHost via) {
        delegate.setRoute(to, via);
    }

    @Override
    public int get(String url) {
        return delegate.get(url);
    }

    @Override
    public int get(String url, String referrer) {
        return delegate.get(url, referrer);
    }

    @Override
    public int post(String url, Map<String, Object> data, ScrapClient.PostType dataType, String charset, String referrer) {
        return delegate.post(url, data, dataType, charset, referrer);
    }

    @Override
    public Exception getException() {
        return delegate.getException();
    }

    @Override
    public String getResponseHeader(String name) {
        return delegate.getResponseHeader(name);
    }

    @Override
    public String getContentAsString() {
        return delegate.getContentAsString();
    }

    @Override
    public byte[] getContent() {
        return delegate.getContent();
    }

    @Override
    public void clearCookies() {
        delegate.clearCookies();
    }

    @Override
    public void addCookie(Cookie cookie) {
        delegate.addCookie(cookie);
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }
}