package com.serphacker.serposcope.scraper.http;

import com.serphacker.serposcope.scraper.http.proxy.ScrapProxy;
import java.io.Closeable;
import java.util.Map;
import org.apache.http.HttpHost;
import org.apache.http.cookie.Cookie;

public interface ScraperHttpClient extends Closeable {

    ScrapProxy getProxy();

    void setProxy(ScrapProxy proxy);

    void setUseragent(String useragent);

    void removeRoutes();

    void setRoute(HttpHost to, HttpHost via);

    int get(String url);

    int get(String url, String referrer);

    int post(String url, Map<String, Object> data, ScrapClient.PostType dataType, String charset, String referrer);

    Exception getException();

    String getResponseHeader(String name);

    String getContentAsString();

    byte[] getContent();

    void clearCookies();

    void addCookie(Cookie cookie);
}