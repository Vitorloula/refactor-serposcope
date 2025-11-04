package com.serphacker.serposcope.scraper.strategy;

import com.google.inject.AbstractModule;

/**
 * Guice module that binds the default scraping engine based on configuration.
 */
public class ScraperModule extends AbstractModule {

    private final String engineId;

    public ScraperModule(String engineId) {
        this.engineId = engineId;
    }

    @Override
    protected void configure() {
        bind(Engine.class).toInstance(Engine.fromString(engineId));
    }
}
