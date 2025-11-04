package com.serphacker.serposcope.scraper.strategy.adapter;

import com.serphacker.serposcope.scraper.google.GoogleScrapResult;
import com.serphacker.serposcope.scraper.google.scraper.GoogleScraper;
import com.serphacker.serposcope.scraper.strategy.SearchEngineScraper;
import com.serphacker.serposcope.scraper.strategy.mapping.RequestMapper;
import com.serphacker.serposcope.scraper.strategy.mapping.ResultMapper;
import com.serphacker.serposcope.scraper.strategy.mapping.ScraperResult;
import com.serphacker.serposcope.scraper.strategy.mapping.SearchRequest;
import java.util.Objects;

/**
 * Adapter that delegates scraping to the legacy Google HTML scraper.
 */
public class GoogleHtmlScraperAdapter implements SearchEngineScraper {

    private final GoogleScraper delegate;

    public GoogleHtmlScraperAdapter(GoogleScraper delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
    }

    public GoogleScraper getDelegate() {
        return delegate;
    }

    @Override
    public ScraperResult fetchResults(SearchRequest request) throws InterruptedException {
        GoogleScrapResult legacyResult = delegate.scrap(RequestMapper.toLegacy(request));
        return ResultMapper.fromLegacy(legacyResult);
    }
}
