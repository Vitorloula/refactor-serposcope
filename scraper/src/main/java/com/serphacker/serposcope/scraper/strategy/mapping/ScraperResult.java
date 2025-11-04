package com.serphacker.serposcope.scraper.strategy.mapping;

import com.serphacker.serposcope.scraper.google.GoogleScrapResult;
import java.util.ArrayList;

/**
 * Container for the result entries along with metadata about the scraping execution.
 */
public class ScraperResult extends ArrayList<ResultEntry> {

    private GoogleScrapResult.Status status = GoogleScrapResult.Status.OK;
    private int captchas;
    private long totalResults;

    public ScraperResult() {
    }

    public ScraperResult(GoogleScrapResult.Status status, int captchas, long totalResults) {
        this.status = status;
        this.captchas = captchas;
        this.totalResults = totalResults;
    }

    public GoogleScrapResult.Status getStatus() {
        return status;
    }

    public void setStatus(GoogleScrapResult.Status status) {
        this.status = status;
    }

    public int getCaptchas() {
        return captchas;
    }

    public void setCaptchas(int captchas) {
        this.captchas = captchas;
    }

    public long getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }
}
