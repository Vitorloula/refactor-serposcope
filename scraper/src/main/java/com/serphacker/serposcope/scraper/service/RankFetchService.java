package com.serphacker.serposcope.scraper.service;

import com.serphacker.serposcope.scraper.http.ScrapClient;
import com.serphacker.serposcope.scraper.strategy.SearchEngineScraper;
import com.serphacker.serposcope.scraper.strategy.mapping.ScraperResult;
import com.serphacker.serposcope.scraper.strategy.mapping.SearchRequest;
import java.util.Objects;

/**
 * High level service that orchestrates the execution of scraping strategies.
 */
public class RankFetchService {

    private final SearchEngineScraper scraper;
    private final ScrapClient httpClient;

    public RankFetchService(SearchEngineScraper scraper, ScrapClient httpClient) {
        this.scraper = Objects.requireNonNull(scraper, "scraper");
        this.httpClient = Objects.requireNonNull(httpClient, "httpClient");
    }

    public ScraperResult run(SearchRequest request) throws InterruptedException {
        return scraper.fetchResults(request);
    }

    public SearchEngineScraper getScraper() {
        return scraper;
    }

    public ScrapClient getHttpClient() {
        return httpClient;
    }
}
