package com.serphacker.serposcope.scraper.strategy;

/**
 * Available scraping strategies.
 */
public enum Engine {
    GOOGLE_HTML,
    GOOGLE_API,
    FAKE,
    RANDOM;

    public static Engine fromString(String value) {
        if (value == null || value.isEmpty()) {
            return GOOGLE_HTML;
        }
        try {
            return Engine.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return GOOGLE_HTML;
        }
    }
}
