package com.serphacker.serposcope.scraper.google.scraper.strategy;

import com.serphacker.serposcope.scraper.google.GoogleScrapResult.Status;
import java.util.List;
import org.jsoup.nodes.Document;

public interface SerpParsingStrategy {

    Status parse(Document document, List<String> urls);

    long parseResultsCount(Document document);

    long extractResultsNumber(String html);

    boolean hasNextPage(Document document);
}
