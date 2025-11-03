/* 
 * Serposcope - SEO rank checker https://serposcope.serphacker.com/
 * 
 * Copyright (c) 2016 SERP Hacker
 * @author Pierre Nogues <support@serphacker.com>
 * @license https://opensource.org/licenses/MIT MIT License
 */
package com.serphacker.serposcope.scraper.http;

import com.serphacker.serposcope.scraper.http.proxy.ScrapProxy;
import org.apache.http.Header;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Configuração imutável para ScrapClient (Builder Pattern)
 */
public class ScrapClientConfig {

    private final String userAgent;
    private final Integer timeoutMS;
    private final int maxResponseLength;
    private final ScrapProxy proxy;
    private final boolean insecureSSL;
    private final int maxRedirect;
    private final List<Header> requestHeaders;

    private ScrapClientConfig(Builder builder) {
        this.userAgent = builder.userAgent;
        this.timeoutMS = builder.timeoutMS;
        this.maxResponseLength = builder.maxResponseLength;
        this.proxy = builder.proxy;
        this.insecureSSL = builder.insecureSSL;
        this.maxRedirect = builder.maxRedirect;
        this.requestHeaders = Collections.unmodifiableList(new ArrayList<>(builder.requestHeaders));
    }

    public String getUserAgent() {
        return userAgent;
    }

    public Integer getTimeoutMS() {
        return timeoutMS;
    }

    public int getMaxResponseLength() {
        return maxResponseLength;
    }

    public ScrapProxy getProxy() {
        return proxy;
    }

    public boolean isInsecureSSL() {
        return insecureSSL;
    }

    public int getMaxRedirect() {
        return maxRedirect;
    }

    public List<Header> getRequestHeaders() {
        return requestHeaders;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String userAgent = ScrapClient.DEFAULT_USER_AGENT;
        private Integer timeoutMS = ScrapClient.DEFAULT_TIMEOUT_MS;
        private int maxResponseLength = ScrapClient.DEFAULT_MAX_RESPONSE_LENGTH;
        private ScrapProxy proxy = null;
        private boolean insecureSSL = false;
        private int maxRedirect = 0;
        private List<Header> requestHeaders = new ArrayList<>();

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder timeout(Integer timeoutMS) {
            this.timeoutMS = timeoutMS;
            return this;
        }

        public Builder maxResponseLength(int maxResponseLength) {
            this.maxResponseLength = maxResponseLength;
            return this;
        }

        public Builder proxy(ScrapProxy proxy) {
            this.proxy = proxy;
            return this;
        }

        public Builder insecureSSL(boolean insecureSSL) {
            this.insecureSSL = insecureSSL;
            return this;
        }

        public Builder maxRedirect(int maxRedirect) {
            this.maxRedirect = maxRedirect;
            return this;
        }

        public Builder followRedirects() {
            this.maxRedirect = 10;
            return this;
        }

        public Builder noRedirects() {
            this.maxRedirect = 0;
            return this;
        }

        public Builder addRequestHeader(Header header) {
            this.requestHeaders.add(header);
            return this;
        }

        public ScrapClientConfig build() {
            validate();
            return new ScrapClientConfig(this);
        }

        private void validate() {
            if (timeoutMS != null && timeoutMS < 0) {
                throw new IllegalArgumentException("Timeout não pode ser negativo: " + timeoutMS);
            }
            if (maxResponseLength < 1024) {
                throw new IllegalArgumentException(
                        "maxResponseLength deve ser >= 1024 bytes, atual: " + maxResponseLength);
            }
            if (maxRedirect < 0) {
                throw new IllegalArgumentException("maxRedirect não pode ser negativo: " + maxRedirect);
            }
        }
    }

    @Override
    public String toString() {
        return "ScrapClientConfig{" +
                "userAgent='" + userAgent + '\'' +
                ", timeoutMS=" + timeoutMS +
                ", maxResponseLength=" + maxResponseLength +
                ", proxy=" + proxy +
                ", insecureSSL=" + insecureSSL +
                ", maxRedirect=" + maxRedirect +
                ", requestHeaders=" + requestHeaders.size() + " headers" +
                '}';
    }
}
