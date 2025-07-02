package org.airtribe.service;

import org.airtribe.config.NewsApiProperties;
import org.airtribe.enums.SupportedLanguages;
import org.airtribe.model.NewsArticlesResponse;
import org.airtribe.model.request.NewsSearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        try {
            URI uri = builder.build().encode().toUri();
            NewsArticlesResponse response = restTemplate.getForObject(uri, NewsArticlesResponse.class);
            return response;
        }
        catch (HttpClientErrorException | HttpServerErrorException ex) {
            LOGGER.error("Error occurred while making api call : {}" , ex.getLocalizedMessage());
            throw ex;
        }
        catch (RestClientException ex) {
            LOGGER.error("Generic Client Exception during api call : {}", ex.getMessage());
            throw ex;
        }
    }

    public List<String> getSupportedLanguages() {
        return Arrays.stream(SupportedLanguages.values()).map(lang -> lang.getCode() + " : " + lang.getLanguage()).collect(Collectors.toList());
    }
}
