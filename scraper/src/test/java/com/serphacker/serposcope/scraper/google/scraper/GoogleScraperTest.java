/*
 * Serposcope - SEO rank checker https://serposcope.serphacker.com/
 *
 * Copyright (c) 2016 SERP Hacker
 * @author Pierre Nogues <support@serphacker.com>
 * @license https://opensource.org/licenses/MIT MIT License
 */
package com.serphacker.serposcope.scraper.google.scraper;
import com.serphacker.serposcope.scraper.google.GoogleScrapResult.Status;

import com.serphacker.serposcope.scraper.ResourceHelper;
import com.serphacker.serposcope.scraper.google.GoogleCountryCode;

import static com.serphacker.serposcope.scraper.google.GoogleScrapResult.Status.ERROR_NETWORK;
import static com.serphacker.serposcope.scraper.google.GoogleScrapResult.Status.OK;

import com.serphacker.serposcope.scraper.google.GoogleScrapResult;
import com.serphacker.serposcope.scraper.google.GoogleScrapSearch;
import com.serphacker.serposcope.scraper.google.scraper.strategy.SerpParsingStrategy;
import com.serphacker.serposcope.scraper.http.ScrapClient;
import com.serphacker.serposcope.scraper.http.ScraperHttpClient;
import com.serphacker.serposcope.scraper.http.proxy.ScrapProxy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;

import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.runner.RunWith;

import org.jsoup.nodes.Document;
import org.apache.http.HttpHost;
import org.apache.http.cookie.Cookie;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author admin
 */
@RunWith(MockitoJUnitRunner.class)
public class GoogleScraperTest {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleScraperTest.class);

    public GoogleScraperTest() {
    }

    @Test
    public void testBuildUrl() {
        
        GoogleScraper scraper = new GoogleScraper((ScrapClient) null, null);
        GoogleScrapSearch search = null;
        String url = null;

        search = new GoogleScrapSearch();
        search.setKeyword("keyword");
        assertEquals("https://www.google.com/search?q=keyword", scraper.buildRequestUrl(search, 0));

        search = new GoogleScrapSearch();
        search.setKeyword("keyword");
        assertEquals("https://www.google.com/search?q=keyword&start=10", scraper.buildRequestUrl(search, 1));

        search = new GoogleScrapSearch();
        search.setKeyword("keyword");
        search.setDatacenter("10.0.0.1");
        assertEquals("https://www.google.com/search?q=keyword", scraper.buildRequestUrl(search, 0));
    }

    @Test
    public void testParseSerpEmpty() throws IOException {
        ScrapClient http = mock(ScrapClient.class);
        when(http.getContentAsString()).thenReturn("");

        GoogleScraper scraper = new GoogleScraper(http, null);
        assertEquals(ERROR_NETWORK, scraper.parseSerp(new ArrayList<>()));
    }

    @Test
    public void testDownloadNetworkError() throws Exception {
        ScrapClient http = mock(ScrapClient.class);
        when(http.get(any(), any())).thenReturn(-1);

        GoogleScrapSearch search = new GoogleScrapSearch();
        search.setKeyword("suivi de position");
        search.setCountry(GoogleCountryCode.FR);

        GoogleScraper scraper = new GoogleScraper(http, null);
        assertEquals(ERROR_NETWORK, scraper.scrap(search).status);
    }

    @Test
    public void testParseNetworkError() throws Exception {
        ScrapClient http = mock(ScrapClient.class);
        when(http.get(any(), any())).thenReturn(200);
        when(http.getContentAsString()).thenReturn("");

        GoogleScrapSearch search = new GoogleScrapSearch();
        search.setKeyword("suivi de position");
        search.setCountry(GoogleCountryCode.FR);

        GoogleScraper scraper = new GoogleScraper(http, null);
        assertEquals(ERROR_NETWORK, scraper.scrap(search).status);
    }

    @Test
    public void testCustomParsingStrategyIsUsed() throws Exception {
        StubHttpClient http = new StubHttpClient();
        RecordingStrategy strategy = new RecordingStrategy();
        strategy.hasNext = false;
        strategy.resultsCount = 0L;
        strategy.urlToAdd = "http://example.com";

        GoogleScraper scraper = new GoogleScraper((ScraperHttpClient) http, null, strategy);

        GoogleScrapSearch search = new GoogleScrapSearch();
        search.setKeyword("keyword");

        GoogleScrapResult result = scraper.scrap(search);

        assertThat(result.status, is(OK));
        assertEquals(1, result.urls.size());
        assertEquals(strategy.urlToAdd, result.urls.get(0));
        assertEquals(1, strategy.parseCalls);
        assertEquals(1, strategy.hasNextCalls);
        assertEquals(1, strategy.parseResultsCountCalls);
    }

    private static class StubHttpClient implements ScraperHttpClient {

        int status = 200;
        String content = "<html></html>";
        String userAgent;
        ScrapProxy proxy;
        boolean cookiesCleared;

        @Override
        public ScrapProxy getProxy() {
            return proxy;
        }

        @Override
        public void setProxy(ScrapProxy proxy) {
            this.proxy = proxy;
        }

        @Override
        public void setUseragent(String useragent) {
            this.userAgent = useragent;
        }

        @Override
        public void removeRoutes() {
        }

        @Override
        public void setRoute(HttpHost to, HttpHost via) {
        }

        @Override
        public int get(String url) {
            return status;
        }

        @Override
        public int get(String url, String referrer) {
            return status;
        }

        @Override
        public int post(String url, Map<String, Object> data, ScrapClient.PostType dataType, String charset, String referrer) {
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

    private static class RecordingStrategy implements SerpParsingStrategy {

        int parseCalls;
        int hasNextCalls;
        int parseResultsCountCalls;
        Status statusToReturn = OK;
        boolean hasNext;
        long resultsCount;
        String urlToAdd;

        @Override
        public Status parse(Document document, List<String> urls) {
            parseCalls++;
            if (urlToAdd != null) {
                urls.add(urlToAdd);
            }
            return statusToReturn;
        }

        @Override
        public long parseResultsCount(Document document) {
            parseResultsCountCalls++;
            return resultsCount;
        }

        @Override
        public long extractResultsNumber(String html) {
            return 0;
        }

        @Override
        public boolean hasNextPage(Document document) {
            hasNextCalls++;
            return hasNext;
        }
    }


    @Test
    public void testBuildUule() {
        GoogleScraper scraper = new GoogleScraper((ScrapClient) null, null);
        assertEquals(
            "w+CAIQICIpTW9udGV1eCxQcm92ZW5jZS1BbHBlcy1Db3RlIGQnQXp1cixGcmFuY2U",
            scraper.buildUule("Monteux,Provence-Alpes-Cote d'Azur,France").replaceAll("=+$", "")
        );
        assertEquals(
            "w+CAIQICIGRnJhbmNl",
            scraper.buildUule("France").replaceAll("=+$", "")
        );
        assertEquals(
            "w+CAIQICIlQ2VudHJlLVZpbGxlLENoYW1wYWduZS1BcmRlbm5lLEZyYW5jZQ",
            scraper.buildUule("Centre-Ville,Champagne-Ardenne,France").replaceAll("=+$", "")
        );
        assertEquals(
            "w+CAIQICIfTGlsbGUsTm9yZC1QYXMtZGUtQ2FsYWlzLEZyYW5jZQ",
            scraper.buildUule("Lille,Nord-Pas-de-Calais,France").replaceAll("=+$", "")
        );
    }

    @Test
    public void extractResults() {
        GoogleScraper scraper = new GoogleScraper((ScrapClient) null, null);
        assertEquals(2490l, scraper.extractResultsNumber("Environ 2 490 résultats"));
//        assertEquals(25270000000l, scraper.extractResultsNumber("Page&nbsp;10 sur environ 25&nbsp;270&nbsp;000&nbsp;000&nbsp;résultats<nobr> (0,46&nbsp;secondes)&nbsp;</nobr>"));
//        assertEquals(25270000000l, scraper.extractResultsNumber("Page 10 of about 25,270,000,000 results<nobr> (0.42 seconds)&nbsp;</nobr>"));
        assertEquals(25270000000l, scraper.extractResultsNumber("About 25,270,000,000 results<nobr> (0.28 seconds)&nbsp;</nobr>"));
        assertEquals(225000l, scraper.extractResultsNumber("About 225,000 results<nobr> (0.87 seconds)&nbsp;</nobr>"));
//        assertEquals(225000l, scraper.extractResultsNumber("Page 5 of about 225,000 results (0.45 seconds) "));
    }

}