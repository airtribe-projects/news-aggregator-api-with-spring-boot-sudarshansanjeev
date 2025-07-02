package org.airtribe.service;

import org.airtribe.config.NewsApiProperties;
import org.airtribe.model.ArticlesItem;
import org.airtribe.model.NewsArticlesResponse;
import org.airtribe.model.Source;
import org.airtribe.model.User;
import org.airtribe.model.request.NewsSearchRequest;
import org.airtribe.repository.PreferenceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsServiceTest {

    @Mock
    RestTemplate restTemplate;

    @Mock
    AggregatorService aggregatorService;

    NewsService realNewsService;
    NewsArticlesResponse newsArticlesResponse;

    @Mock
    PreferenceRepository preferenceRepository;

    @BeforeEach
    void setUp() {
        newsArticlesResponse = new NewsArticlesResponse();
        ArticlesItem articlesItem = new ArticlesItem();
        articlesItem.setAuthor("Sample author");
        articlesItem.setContent("Sample content");
        articlesItem.setDescription("Sample Description");
        Source source = new Source();
        source.setId("ID");
        source.setName("BBC");
        articlesItem.setSource(source);
        articlesItem.setPublishedAt("24-Jul");

        newsArticlesResponse.setArticles(List.of(articlesItem));
        newsArticlesResponse.setTotalResults(1);
        newsArticlesResponse.setStatus("ok");

        NewsApiProperties newsApiProperties = new NewsApiProperties();
        newsApiProperties.setBaseUrl("https://newsapi.org/v2");
        newsApiProperties.setApiKey("test-api-key");

        realNewsService = new NewsService(restTemplate, newsApiProperties);
        realNewsService.aggregatorService = aggregatorService; // manually inject mock
        realNewsService.preferenceRepository = preferenceRepository; // inject preference repo
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFetchNewsArticles() {
        NewsSearchRequest request = new NewsSearchRequest();
        request.setQuery(List.of("Donald Trump"));

        when(restTemplate.getForObject(any(URI.class), eq(NewsArticlesResponse.class))).thenReturn(newsArticlesResponse);

        NewsArticlesResponse actual = realNewsService.fetchNewsArticles(request);
        assertEquals(newsArticlesResponse, actual);
    }

    @Test
    public void testFetchNewsArticles_QueryParams_Verification() {
        NewsSearchRequest request = new NewsSearchRequest();
        request.setQuery(List.of("usa", "india"));
        request.setPage(1);
        request.setPageSize(2);

        NewsArticlesResponse dummyResponse = new NewsArticlesResponse();
        when(restTemplate.getForObject(any(URI.class), eq(NewsArticlesResponse.class))).thenReturn(dummyResponse);

        realNewsService.fetchNewsArticles(request);

        ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
        verify(restTemplate).getForObject(uriCaptor.capture(), eq(NewsArticlesResponse.class));
        URI capturedUri = uriCaptor.getValue();
        String finalUri = capturedUri.toString();

        // Use encoded value "q=usa+AND+india"
        assertTrue(finalUri.contains("q=usa+AND+india"), "Query parameter should contain joined keywords");
        assertTrue(finalUri.contains("pageSize=2"), "Should contain correct pageSize");
        assertTrue(finalUri.contains("page=1"), "Should contain correct page number");
        assertTrue(finalUri.contains("apiKey=test-api-key"), "Should contain API key");
    }

    @Test
    void fetchNewsBasedOnPreferences() {
        User someUser = new User();
        someUser.setUsername("Some User");
        someUser.setId(1L);

        when(aggregatorService.getCurrentUser()).thenReturn(someUser);

        NewsArticlesResponse mockedResponse = new NewsArticlesResponse();
        mockedResponse.setArticles(Collections.emptyList());
        mockedResponse.setStatus("ok");
        mockedResponse.setTotalResults(0);

        when(restTemplate.getForObject(any(URI.class), eq(NewsArticlesResponse.class))).thenReturn(mockedResponse);

        NewsArticlesResponse actual = realNewsService.fetchNewsBasedOnPreferences();
        assertNotNull(actual);
        assertEquals(0, actual.getArticles().size());
    }
}