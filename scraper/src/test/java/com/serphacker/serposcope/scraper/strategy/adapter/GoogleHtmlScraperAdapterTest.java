package com.serphacker.serposcope.scraper.strategy.adapter;

import com.serphacker.serposcope.scraper.google.GoogleScrapResult;
import com.serphacker.serposcope.scraper.google.GoogleScrapSearch;
import com.serphacker.serposcope.scraper.google.GoogleScrapResult.Status;
import com.serphacker.serposcope.scraper.google.scraper.GoogleScraper;
import com.serphacker.serposcope.scraper.strategy.mapping.ResultMapper;
import com.serphacker.serposcope.scraper.strategy.mapping.ScraperResult;
import com.serphacker.serposcope.scraper.strategy.mapping.SearchRequest;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GoogleHtmlScraperAdapterTest {

    @Mock
    GoogleScraper delegate;

    @Test
    public void delegatesToLegacyScraper() throws Exception {
        SearchRequest request = SearchRequest.builder()
            .keyword("serposcope")
            .pages(2)
            .resultsPerPage(50)
            .pause(1000L, 2000L)
            .build();

        GoogleScrapResult legacyResult = new GoogleScrapResult(Status.OK, Arrays.asList("http://a", "http://b"), 2, 120L);
        when(delegate.scrap(any(GoogleScrapSearch.class))).thenReturn(legacyResult);

        GoogleHtmlScraperAdapter adapter = new GoogleHtmlScraperAdapter(delegate);
        ScraperResult result = adapter.fetchResults(request);

        ArgumentCaptor<GoogleScrapSearch> captor = ArgumentCaptor.forClass(GoogleScrapSearch.class);
        verify(delegate).scrap(captor.capture());

        GoogleScrapSearch search = captor.getValue();
        assertEquals(request.getKeyword(), search.getKeyword());
        assertEquals(request.getPages(), search.getPages());
        assertEquals(request.getResultsPerPage(), search.getResultPerPage());

        assertEquals(Status.OK, result.getStatus());
        assertEquals(legacyResult.captchas, result.getCaptchas());
        assertEquals(legacyResult.googleResults, result.getTotalResults());
        assertThat(result, hasSize(legacyResult.urls.size()));

        GoogleScrapResult roundTrip = ResultMapper.toLegacy(result);
        assertEquals(legacyResult, roundTrip);
    }
}
