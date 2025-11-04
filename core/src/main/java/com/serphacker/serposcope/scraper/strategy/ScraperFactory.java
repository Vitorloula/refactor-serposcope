package com.serphacker.serposcope.scraper.strategy;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.serphacker.serposcope.di.GoogleScraperFactory;
import com.serphacker.serposcope.scraper.captcha.solver.CaptchaSolver;
import com.serphacker.serposcope.scraper.google.scraper.FakeGScraper;
import com.serphacker.serposcope.scraper.google.scraper.GoogleScraper;
import com.serphacker.serposcope.scraper.google.scraper.RandomGScraper;
import com.serphacker.serposcope.scraper.http.ScrapClient;
import com.serphacker.serposcope.scraper.strategy.adapter.FakeScraperAdapter;
import com.serphacker.serposcope.scraper.strategy.adapter.GoogleApiScraper;
import com.serphacker.serposcope.scraper.strategy.adapter.GoogleHtmlScraperAdapter;
import com.serphacker.serposcope.scraper.strategy.adapter.RandomScraperAdapter;
import com.serphacker.serposcope.scraper.strategy.SearchEngineScraper;

/**
 * Factory responsible for instantiating the configured scraping strategy.
 */
@Singleton
public class ScraperFactory {

    private final Engine defaultEngine;
    private final GoogleScraperFactory googleScraperFactory;

    @Inject
    public ScraperFactory(Engine engine, GoogleScraperFactory googleScraperFactory) {
        this.defaultEngine = engine == null ? Engine.GOOGLE_HTML : engine;
        this.googleScraperFactory = googleScraperFactory;
    }

    public SearchEngineScraper create(ScrapClient client, CaptchaSolver solver) {
        return create(defaultEngine, client, solver);
    }

    public SearchEngineScraper create(Engine engine, ScrapClient client, CaptchaSolver solver) {
        Engine effectiveEngine = engine == null ? defaultEngine : engine;
        switch (effectiveEngine) {
            case GOOGLE_API:
                return new GoogleApiScraper();
            case FAKE:
                return new FakeScraperAdapter(new FakeGScraper(client, solver));
            case RANDOM:
                return new RandomScraperAdapter(new RandomGScraper(client, solver));
            case GOOGLE_HTML:
            default:
                GoogleScraper delegate = googleScraperFactory != null
                    ? googleScraperFactory.get(client, solver)
                    : new GoogleScraper(client, solver);
                return new GoogleHtmlScraperAdapter(delegate);
        }
    }

    public Engine getDefaultEngine() {
        return defaultEngine;
    }
}
