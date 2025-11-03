package com.serphacker.serposcope.scraper.http.factory;

import org.apache.http.client.methods.HttpRequestBase;

public abstract class HttpRequestFactory {

    protected final String url;
    protected final String userAgent;

    public HttpRequestFactory(String url, String userAgent) {
        this.url = url;
        this.userAgent = userAgent;
    }

    public final HttpRequestBase create() {
        HttpRequestBase request = createRequest();
        configureRequest(request);
        return request;
    }

    protected abstract HttpRequestBase createRequest();

    protected void configureRequest(HttpRequestBase request) {
        if (request.getFirstHeader("user-agent") == null) {
            request.setHeader("User-Agent", userAgent);
        }
    }
}
