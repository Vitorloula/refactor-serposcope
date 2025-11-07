package com.serphacker.serposcope.scraper;

import com.serphacker.serposcope.scraper.google.GoogleScrapResult;
import com.serphacker.serposcope.scraper.google.GoogleScrapSearch;
import com.serphacker.serposcope.scraper.google.GoogleScrapResult.Status;
import com.serphacker.serposcope.scraper.google.scraper.strategy.SerpParsingStrategy;
import com.serphacker.serposcope.scraper.http.PostType;
import com.serphacker.serposcope.scraper.http.ScraperHttpClient;
import com.serphacker.serposcope.scraper.http.proxy.ScrapProxy;
import java.util.List;
import java.util.Map;
import org.jsoup.nodes.Document;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertSame;
import org.apache.http.HttpHost;
import org.apache.http.cookie.Cookie;

public class ScraperFacadeTest {

    private static class StubStrategy implements SerpParsingStrategy {

        @Override
        public Status parse(Document document, List<String> urls) {
            urls.add("http://example.com");
            return Status.OK;
        }

        @Override
        public long parseResultsCount(Document document) {
            return 1L;
        }

        @Override
        public long extractResultsNumber(String html) {
            return 0L;
        }

        @Override
        public boolean hasNextPage(Document document) {
            return false;
        }
    }

    @Test
    public void testScrapeGoogleDelegatesThroughFacade() throws Exception {
        RecordingHttpClient httpClient = new RecordingHttpClient();
        StubStrategy strategy = new StubStrategy();

        ScraperFacade facade = new ScraperFacade(httpClient, null, strategy);

        facade.setUserAgent("ua");
        assertEquals("ua", httpClient.userAgent);

        facade.clearCookies();
        assertTrue(httpClient.cookiesCleared);

        ScrapProxy proxy = new ScrapProxy() {
            @Override
            public boolean hasAttr(String key) {
                return false;
            }

            @Override
            public void setAttr(String key, Object value) {
            }

            @Override
            public <T> T getAttr(String key, Class<T> clazz) {
                return null;
            }

            @Override
            public void removeAttr(String key) {
            }

            @Override
            public void clearAttrs() {
            }
        };
        facade.setProxy(proxy);
        assertSame(proxy, httpClient.proxy);

        GoogleScrapSearch search = new GoogleScrapSearch();
        search.setKeyword("keyword");

        GoogleScrapResult result = facade.scrapeGoogle(search);

        assertEquals(Status.OK, result.status);
        assertEquals(1, result.urls.size());
        assertEquals("http://example.com", result.urls.get(0));
        assertSame(strategy, facade.getParsingStrategy());
        assertEquals(1, httpClient.getCalls);
    }

    private static class RecordingHttpClient implements ScraperHttpClient {

        int status = 200;
        String content = "<html></html>";
        String userAgent;
        boolean cookiesCleared;
        int getCalls;
        ScrapProxy proxy;

        @Override
        public ScrapProxy getProxy() {
            return proxy;
        }

        @Override
        public void setProxy(ScrapProxy proxy) {
            this.proxy = proxy;
        }

        @Override
        public void setUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }

        @Override
        public void removeRoutes() {
        }

        @Override
        public void setRoute(HttpHost to, HttpHost via) {
        }

        @Override
        public int get(String url) {
            getCalls++;
            return status;
        }

        @Override
        public int get(String url, String referrer) {
            getCalls++;
            return status;
        }

        @Override
        public int post(String url, Map<String, Object> data, PostType dataType, String charset, String referrer) {
            return 0;
        }

        @Override
        public Exception getException() {
            return null;
        }

        @Override
        public String getResponseHeader(String name) {
            return null;
        }

        @Override
        public String getContentAsString() {
            return content;
        }

        @Override
        public byte[] getContent() {
            return null;
        }

        @Override
        public void clearCookies() {
            cookiesCleared = true;
        }

        @Override
        public void addCookie(Cookie cookie) {
        }

        @Override
        public void close() {
        }
    }
}
