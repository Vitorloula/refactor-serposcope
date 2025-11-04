package com.serphacker.serposcope.scraper.strategy.mapping;

import com.serphacker.serposcope.scraper.google.GoogleScrapSearch;

/**
 * Utility methods to convert between the new SearchRequest DTO and the legacy GoogleScrapSearch.
 */
public final class RequestMapper {

    private RequestMapper() {
    }

    public static GoogleScrapSearch toLegacy(SearchRequest request) {
        GoogleScrapSearch search = new GoogleScrapSearch();
        if (request == null) {
            return search;
        }

        search.setKeyword(request.getKeyword());
        search.setCountry(request.getCountry());
        search.setDatacenter(request.getDatacenter());
        search.setDevice(request.getDevice());
        search.setLocal(request.getLocal());
        search.setCustomParameters(request.getCustomParameters());
        search.setPages(request.getPages());
        search.setResultPerPage(request.getResultsPerPage());
        search.setPagePauseMS(request.getMinPauseBetweenPageMs(), request.getMaxPauseBetweenPageMs());
        return search;
    }

    public static SearchRequest fromLegacy(GoogleScrapSearch search) {
        if (search == null) {
            return SearchRequest.builder().build();
        }

        return SearchRequest.builder()
            .keyword(search.getKeyword())
            .country(search.getCountry())
            .datacenter(search.getDatacenter())
            .device(search.getDevice())
            .local(search.getLocal())
            .customParameters(search.getCustomParameters())
            .pages(search.getPages())
            .resultsPerPage(search.getResultPerPage())
            .pause(0L, 0L)
            .build();
    }
}
