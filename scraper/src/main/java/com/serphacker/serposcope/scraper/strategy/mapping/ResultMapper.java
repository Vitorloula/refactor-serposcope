package com.serphacker.serposcope.scraper.strategy.mapping;

import com.serphacker.serposcope.scraper.google.GoogleScrapResult;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods to convert between legacy GoogleScrapResult objects and the strategy DTOs.
 */
public final class ResultMapper {

    private ResultMapper() {
    }

    public static ScraperResult fromLegacy(GoogleScrapResult legacyResult) {
        ScraperResult result = new ScraperResult();
        if (legacyResult == null) {
            result.setStatus(GoogleScrapResult.Status.ERROR_NETWORK);
            return result;
        }

        result.setStatus(legacyResult.status);
        result.setCaptchas(legacyResult.captchas);
        result.setTotalResults(legacyResult.googleResults);

        List<String> urls = legacyResult.urls;
        if (urls != null) {
            int position = 1;
            for (String url : urls) {
                result.add(new ResultEntry(position++, url, null));
            }
        }
        return result;
    }

    public static GoogleScrapResult toLegacy(ScraperResult scraperResult) {
        if (scraperResult == null) {
            return new GoogleScrapResult(GoogleScrapResult.Status.ERROR_NETWORK, new ArrayList<>(), 0);
        }

        List<String> urls = new ArrayList<>();
        for (ResultEntry entry : scraperResult) {
            urls.add(entry.getUrl());
        }
        return new GoogleScrapResult(scraperResult.getStatus(), urls, scraperResult.getCaptchas(), scraperResult.getTotalResults());
    }
}
