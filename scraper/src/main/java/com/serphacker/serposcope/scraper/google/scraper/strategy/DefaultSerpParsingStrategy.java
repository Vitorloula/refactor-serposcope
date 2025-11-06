package com.serphacker.serposcope.scraper.google.scraper.strategy;

import com.serphacker.serposcope.scraper.google.GoogleScrapResult.Status;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Default implementation that reproduces the legacy SERP parsing logic.
 */
public class DefaultSerpParsingStrategy implements SerpParsingStrategy {

    @Override
    public Status parse(Document document, List<String> urls) {
        if (document == null) {
            return Status.ERROR_NETWORK;
        }

        Element resDiv = document.getElementById("res");
        if (resDiv != null) {
            return parseSerpLayoutRes(resDiv, urls);
        }

        Element mainDiv = document.getElementById("main");
        if (mainDiv != null) {
            return parseSerpLayoutMain(mainDiv, urls);
        }

        return Status.ERROR_PARSING;
    }

    @Override
    public long parseResultsCount(Document document) {
        if (document == null) {
            return 0;
        }

        Element resultStatsDiv = document.getElementById("resultStats");
        if (resultStatsDiv == null) {
            return 0;
        }

        return extractResultsNumber(resultStatsDiv.html());
    }

    @Override
    public long extractResultsNumber(String html) {
        if (html == null || html.isEmpty()) {
            return 0;
        }
        html = html.replaceAll("\\(.+\\)", "");
        html = html.replaceAll("[^0-9]+", "");
        if (!html.isEmpty()) {
            return Long.parseLong(html);
        }
        return 0;
    }

    @Override
    public boolean hasNextPage(Document document) {
        if (document == null) {
            return false;
        }

        if (document.getElementById("pnnext") != null) {
            return true;
        }

        Elements navends = document.getElementsByClass("navend");
        if (navends.size() > 1
            && navends.last().children().size() > 0
            && "a".equals(navends.last().child(0).tagName())) {
            return true;
        }

        Elements footerLinks = document.select("footer a");
        return footerLinks.stream().anyMatch(e -> e.text().endsWith(">"));
    }

    private Status parseSerpLayoutRes(Element resElement, List<String> urls) {
        Elements h3Elts = resElement.select("a > h3");
        if (h3Elts.isEmpty()) {
            return parseSerpLayoutResLegacy(resElement, urls);
        }

        for (Element h3Elt : h3Elts) {
            String link = extractLink(h3Elt.parent());
            if (link == null) {
                continue;
            }
            urls.add(link);
        }

        return Status.OK;
    }

    private Status parseSerpLayoutResLegacy(Element resElement, List<String> urls) {
        Elements h3Elts = resElement.getElementsByTag("h3");
        for (Element h3Elt : h3Elts) {

            if (isSiteLinkElement(h3Elt)) {
                continue;
            }

            String link = extractLink(h3Elt.parent());
            if (link == null) {
                link = extractLink(h3Elt.getElementsByTag("a").first());
            }
            if (link != null) {
                urls.add(link);
            }
        }

        return Status.OK;
    }

    private Status parseSerpLayoutMain(Element divElement, List<String> urls) {
        Elements links = divElement.select(
            "#main > div > div:first-child > div:first-child > a:first-child,"
                + "#main > div > div:first-child > a:first-child"
        );
        if (links.isEmpty()) {
            return parseSerpLayoutResLegacy(divElement, urls);
        }

        for (Element link : links) {
            if (!link.children().isEmpty() && "img".equals(link.child(0).tagName())) {
                continue;
            }

            String url = extractLink(link);
            if (url == null) {
                continue;
            }

            urls.add(url);
        }

        return Status.OK;
    }

    private boolean isSiteLinkElement(Element element) {
        if (element == null) {
            return false;
        }

        Elements parents = element.parents();
        if (parents == null || parents.isEmpty()) {
            return false;
        }

        for (Element parent : parents) {
            if (parent.hasClass("mslg") || parent.hasClass("nrg") || parent.hasClass("nrgw")) {
                return true;
            }
        }

        return false;
    }

    private String extractLink(Element element) {
        if (element == null) {
            return null;
        }

        String attr = element.attr("href");
        if (attr == null) {
            return null;
        }

        if (attr.startsWith("http://www.google") || attr.startsWith("https://www.google")) {
            if (attr.contains("/aclk?")) {
                return null;
            }
        }

        if (attr.startsWith("http://") || attr.startsWith("https://")) {
            return attr;
        }

        if (attr.startsWith("/url?")) {
            try {
                List<NameValuePair> parse = URLEncodedUtils.parse(attr.substring(5), StandardCharsets.UTF_8);
                Map<String, String> map = parse.stream()
                    .collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
                return map.get("q");
            } catch (Exception ex) {
                return null;
            }
        }

        return null;
    }
}
