package com.serphacker.serposcope.scraper.strategy.adapter;

import com.serphacker.serposcope.scraper.google.GoogleScrapResult;
import com.serphacker.serposcope.scraper.google.GoogleScrapSearch;
import com.serphacker.serposcope.scraper.google.GoogleScrapResult.Status;
import com.serphacker.serposcope.scraper.google.scraper.RandomGScraper;
import com.serphacker.serposcope.scraper.strategy.mapping.ResultMapper;
import com.serphacker.serposcope.scraper.strategy.mapping.ScraperResult;
import com.serphacker.serposcope.scraper.strategy.mapping.SearchRequest;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RandomScraperAdapterTest {

    @Mock
    RandomGScraper delegate;

    @Test
    public void mapsLegacyResult() throws Exception {
        SearchRequest request = SearchRequest.builder()
            .keyword("random")
            .pages(3)
            .resultsPerPage(5)
            .build();

        GoogleScrapResult legacy = new GoogleScrapResult(Status.OK, Arrays.asList("http://1", "http://2"), 5, 200);
        when(delegate.scrap(any(GoogleScrapSearch.class))).thenReturn(legacy);

        RandomScraperAdapter adapter = new RandomScraperAdapter(delegate);
        ScraperResult result = adapter.fetchResults(request);

        ArgumentCaptor<GoogleScrapSearch> captor = ArgumentCaptor.forClass(GoogleScrapSearch.class);
        verify(delegate).scrap(captor.capture());
        assertEquals(request.getPages(), captor.getValue().getPages());

        GoogleScrapResult roundTrip = ResultMapper.toLegacy(result);
        assertEquals(legacy, roundTrip);
    }
}
