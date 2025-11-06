package com.serphacker.serposcope.scraper;

import com.serphacker.serposcope.scraper.captcha.solver.CaptchaSolver;
import com.serphacker.serposcope.scraper.google.GoogleScrapResult;
import com.serphacker.serposcope.scraper.google.GoogleScrapSearch;
import com.serphacker.serposcope.scraper.google.scraper.GoogleScraper;
import com.serphacker.serposcope.scraper.google.scraper.strategy.DefaultSerpParsingStrategy;
import com.serphacker.serposcope.scraper.google.scraper.strategy.SerpParsingStrategy;
import com.serphacker.serposcope.scraper.http.ScrapClient;
import com.serphacker.serposcope.scraper.http.ScraperHttpClient;
import com.serphacker.serposcope.scraper.http.adapter.ScrapClientAdapter;
import com.serphacker.serposcope.scraper.http.proxy.ScrapProxy;

/**
 * Facade that exposes simplified operations to configure and run scrapers.
 */
public class ScraperFacade {

    private final ScraperHttpClient httpClient;
    private final CaptchaSolver solver;
    private SerpParsingStrategy parsingStrategy;

    public ScraperFacade(ScrapClient httpClient, CaptchaSolver solver) {
        this(httpClient == null ? null : new ScrapClientAdapter(httpClient), solver, new DefaultSerpParsingStrategy());
    }

    public ScraperFacade(ScraperHttpClient httpClient, CaptchaSolver solver, SerpParsingStrategy parsingStrategy) {
        this.httpClient = httpClient;
        this.solver = solver;
        this.parsingStrategy = parsingStrategy == null ? new DefaultSerpParsingStrategy() : parsingStrategy;
    }

    public GoogleScrapResult scrapeGoogle(GoogleScrapSearch search) throws InterruptedException {
        ensureHttpClient();
        return createGoogleScraper().scrap(search);
    }

    public GoogleScraper createGoogleScraper() {
        ensureHttpClient();
        return new GoogleScraper(httpClient, solver, parsingStrategy);
    }

    public void setProxy(ScrapProxy proxy) {
        if (httpClient != null) {
            httpClient.setProxy(proxy);
        }
    }

    public void clearCookies() {
        if (httpClient != null) {
            httpClient.clearCookies();
        }
    }

    public void setUserAgent(String userAgent) {
        if (httpClient != null) {
            httpClient.setUseragent(userAgent);
        }
    }

    public ScraperHttpClient getHttpClient() {
        return httpClient;
    }

    public CaptchaSolver getSolver() {
        return solver;
    }

    public SerpParsingStrategy getParsingStrategy() {
        return parsingStrategy;
    }

    public void setParsingStrategy(SerpParsingStrategy parsingStrategy) {
        this.parsingStrategy = parsingStrategy == null ? new DefaultSerpParsingStrategy() : parsingStrategy;
    }

    private void ensureHttpClient() {
        if (httpClient == null) {
            throw new IllegalStateException("HTTP client is not configured for the scraper facade.");
        }
    }
}