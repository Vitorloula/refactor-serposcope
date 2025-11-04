package com.serphacker.serposcope.scraper.strategy.adapter;

import com.serphacker.serposcope.scraper.google.GoogleScrapResult;
import com.serphacker.serposcope.scraper.google.scraper.RandomGScraper;
import com.serphacker.serposcope.scraper.strategy.SearchEngineScraper;
import com.serphacker.serposcope.scraper.strategy.mapping.RequestMapper;
import com.serphacker.serposcope.scraper.strategy.mapping.ResultMapper;
import com.serphacker.serposcope.scraper.strategy.mapping.ScraperResult;
import com.serphacker.serposcope.scraper.strategy.mapping.SearchRequest;
import java.util.Objects;

/**
 * Adapter around the RandomGScraper.
 */
public class RandomScraperAdapter implements SearchEngineScraper {

    private final RandomGScraper delegate;

    public RandomScraperAdapter(RandomGScraper delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
    }

    public RandomGScraper getDelegate() {
        return delegate;
    }

    @Override
    public ScraperResult fetchResults(SearchRequest request) throws InterruptedException {
        GoogleScrapResult legacyResult = delegate.scrap(RequestMapper.toLegacy(request));
        return ResultMapper.fromLegacy(legacyResult);
    }
}
