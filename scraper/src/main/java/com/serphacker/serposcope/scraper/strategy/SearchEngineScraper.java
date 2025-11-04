package com.serphacker.serposcope.scraper.strategy;

import com.serphacker.serposcope.scraper.strategy.mapping.SearchRequest;
import com.serphacker.serposcope.scraper.strategy.mapping.ScraperResult;

/**
 * Defines a strategy capable of fetching SERP results for a given search request.
 */
public interface SearchEngineScraper {

    /**
     * Executes the scraping routine for the supplied request.
     *
     * @param request parameters describing the search to execute
     * @return the scraping result containing rank entries and metadata
     * @throws InterruptedException when the underlying scraper is interrupted
     */
    ScraperResult fetchResults(SearchRequest request) throws InterruptedException;
}
