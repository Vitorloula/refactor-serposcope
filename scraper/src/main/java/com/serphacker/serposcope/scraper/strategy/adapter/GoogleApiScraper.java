package com.serphacker.serposcope.scraper.strategy.adapter;

import com.serphacker.serposcope.scraper.strategy.SearchEngineScraper;
import com.serphacker.serposcope.scraper.strategy.mapping.ScraperResult;
import com.serphacker.serposcope.scraper.strategy.mapping.SearchRequest;

/**
 * Placeholder implementation for API based SERP providers.
 * Replace the body of {@link #fetchResults(SearchRequest)} with real API calls when available.
 */
public class GoogleApiScraper implements SearchEngineScraper {

    @Override
    public ScraperResult fetchResults(SearchRequest request) {
        return new ScraperResult();
    }
}
