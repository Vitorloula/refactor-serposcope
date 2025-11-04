package com.serphacker.serposcope.scraper.strategy.adapter;

import com.serphacker.serposcope.scraper.google.GoogleScrapResult;
import com.serphacker.serposcope.scraper.google.GoogleScrapSearch;
import com.serphacker.serposcope.scraper.google.GoogleScrapResult.Status;
import com.serphacker.serposcope.scraper.google.scraper.FakeGScraper;
import com.serphacker.serposcope.scraper.strategy.mapping.ResultMapper;
import com.serphacker.serposcope.scraper.strategy.mapping.ScraperResult;
import com.serphacker.serposcope.scraper.strategy.mapping.SearchRequest;
import java.util.Collections;
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
public class FakeScraperAdapterTest {

    @Mock
    FakeGScraper delegate;

    @Test
    public void preservesLegacyBehaviour() throws Exception {
        SearchRequest request = SearchRequest.builder()
            .keyword("demo")
            .resultsPerPage(10)
            .pages(1)
            .build();

        GoogleScrapResult legacy = new GoogleScrapResult(Status.OK, Collections.singletonList("http://demo"), 0, 0);
        when(delegate.scrap(any(GoogleScrapSearch.class))).thenReturn(legacy);

        FakeScraperAdapter adapter = new FakeScraperAdapter(delegate);
        ScraperResult result = adapter.fetchResults(request);

        ArgumentCaptor<GoogleScrapSearch> captor = ArgumentCaptor.forClass(GoogleScrapSearch.class);
        verify(delegate).scrap(captor.capture());
        assertEquals(request.getKeyword(), captor.getValue().getKeyword());

        GoogleScrapResult roundTrip = ResultMapper.toLegacy(result);
        assertEquals(legacy, roundTrip);
    }
}
