package com.serphacker.serposcope.scraper.http.factory;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.junit.Test;
import static org.junit.Assert.*;

public class HttpRequestFactoryTest {

    @Test
    public void testGetRequestFactory() {
        GetRequestFactory factory = new GetRequestFactory(
                "http://example.com",
                "TestAgent");

        HttpRequestBase request = factory.create();

        assertTrue(request instanceof HttpGet);
        assertEquals("http://example.com", request.getURI().toString());
        assertEquals("TestAgent", request.getFirstHeader("User-Agent").getValue());
    }

    @Test
    public void testGetRequestFactoryWithReferrer() {
        GetRequestFactory factory = new GetRequestFactory(
                "http://example.com",
                "TestAgent",
                "http://referrer.com");

        HttpRequestBase request = factory.create();

        assertEquals("http://referrer.com", request.getFirstHeader("Referer").getValue());
    }

    @Test
    public void testPostRequestFactory() throws Exception {
        HttpEntity entity = new StringEntity("test data");

        PostRequestFactory factory = new PostRequestFactory(
                "http://example.com",
                "TestAgent",
                entity);

        HttpRequestBase request = factory.create();

        assertTrue(request instanceof HttpPost);
        assertEquals("http://example.com", request.getURI().toString());
        assertEquals("TestAgent", request.getFirstHeader("User-Agent").getValue());
        assertNotNull(((HttpPost) request).getEntity());
    }

    @Test
    public void testPostRequestFactoryWithReferrer() throws Exception {
        HttpEntity entity = new StringEntity("test data");

        PostRequestFactory factory = new PostRequestFactory(
                "http://example.com",
                "TestAgent",
                entity,
                "http://referrer.com");

        HttpRequestBase request = factory.create();

        assertEquals("http://referrer.com", request.getFirstHeader("Referer").getValue());
    }
}
