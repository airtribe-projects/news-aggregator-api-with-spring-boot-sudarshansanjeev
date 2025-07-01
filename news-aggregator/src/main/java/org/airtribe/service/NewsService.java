package org.airtribe.service;

import org.airtribe.config.NewsApiProperties;
import org.airtribe.model.NewsArticlesResponse;
import org.airtribe.model.request.NewsSearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
public class NewsService {


    RestTemplate restTemplate;


    NewsApiProperties newsApiProperties;

    private final Logger LOGGER = LoggerFactory.getLogger(NewsService.class);
    public NewsService(RestTemplate restTemplate, NewsApiProperties newsApiProperties) {
        this.restTemplate = restTemplate;
        this.newsApiProperties = newsApiProperties;
    }

    public NewsArticlesResponse fetchNewsArticles(NewsSearchRequest request) {
        LOGGER.info("Fetch new request received : {}", request);
        String query = String.join(" AND ", request.getQuery());
        String url = newsApiProperties.getBaseUrl() + "/" + "everything";
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("q" , query);
        queryParams.put("apiKey", newsApiProperties.getApiKey());
        queryParams.put("pageSize", String.valueOf(request.getPageSize()));
        queryParams.put("page", String.valueOf(request.getPage()));

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        queryParams.forEach(builder::queryParam);

        URI uri = builder.build().encode().toUri();
        NewsArticlesResponse response = restTemplate.getForObject(uri, NewsArticlesResponse.class);
        return response;
    }
}
