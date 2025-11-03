package com.serphacker.serposcope.scraper.http.factory;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

public class GetRequestFactory extends HttpRequestFactory {

    private final String referrer;

    public GetRequestFactory(String url, String userAgent, String referrer) {
        super(url, userAgent);
        this.referrer = referrer;
    }

    public GetRequestFactory(String url, String userAgent) {
        this(url, userAgent, null);
    }

    @Override
    protected HttpRequestBase createRequest() {
        return new HttpGet(url);
    }

    @Override
    protected void configureRequest(HttpRequestBase request) {
        super.configureRequest(request);
        if (referrer != null) {
            request.addHeader("Referer", referrer);
        }
    }
}
