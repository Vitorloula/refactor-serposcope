package com.serphacker.serposcope.scraper.http.factory;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;

public class PostRequestFactory extends HttpRequestFactory {

    private final HttpEntity entity;
    private final String referrer;

    public PostRequestFactory(String url, String userAgent, HttpEntity entity, String referrer) {
        super(url, userAgent);
        this.entity = entity;
        this.referrer = referrer;
    }

    public PostRequestFactory(String url, String userAgent, HttpEntity entity) {
        this(url, userAgent, entity, null);
    }

    @Override
    protected HttpRequestBase createRequest() {
        HttpPost post = new HttpPost(url);
        if (entity != null) {
            post.setEntity(entity);
        }
        return post;
    }

    @Override
    protected void configureRequest(HttpRequestBase request) {
        super.configureRequest(request);
        if (referrer != null) {
            request.addHeader("Referer", referrer);
        }
    }
}
