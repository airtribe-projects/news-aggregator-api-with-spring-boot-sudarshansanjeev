package org.airtribe.service;

import org.airtribe.config.NewsApiProperties;
import org.airtribe.enums.SupportedLanguages;
import org.airtribe.model.NewsArticlesResponse;
import org.airtribe.model.Preferences;
import org.airtribe.model.User;
import org.airtribe.model.request.NewsSearchRequest;
import org.airtribe.repository.PreferenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NewsService {


    public static final String QUERY_STRING = "q";
    public static final String QUERY_REGION = "region";
    public static final String QUERY_LANGUAGE = "language";
    public static final String API_KEY = "apiKey";
    public static final String PAGE_SIZE = "pageSize";
    public static final String PAGE = "page";
    public static final String DEFAULT_PAGE_SIZE = "20";
    RestTemplate restTemplate;


    NewsApiProperties newsApiProperties;

    @Autowired
    PreferenceRepository preferenceRepository;

    @Autowired
    AggregatorService aggregatorService;

    private final Logger LOGGER = LoggerFactory.getLogger(NewsService.class);
    public NewsService(RestTemplate restTemplate, NewsApiProperties newsApiProperties) {
        this.restTemplate = restTemplate;
        this.newsApiProperties = newsApiProperties;
    }

    /**
     * This method makes an api call with the query parameters to fetch news given by User
     * @param request
     * @return
     */
    public NewsArticlesResponse fetchNewsArticles(NewsSearchRequest request) {
        LOGGER.info("Fetch new request received : {}", request);
        long startTime = System.currentTimeMillis();
        String query = String.join(" AND ", request.getQuery());
        String url = newsApiProperties.getBaseUrl() + "/" + "everything";
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(QUERY_STRING , query);
        queryParams.put(API_KEY, newsApiProperties.getApiKey());
        queryParams.put(PAGE_SIZE, String.valueOf(request.getPageSize()));
        queryParams.put(PAGE, String.valueOf(request.getPage()));

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        queryParams.forEach(builder::queryParam);

        try {
            URI uri = builder.build().encode().toUri();
            NewsArticlesResponse response = restTemplate.getForObject(uri, NewsArticlesResponse.class);
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            LOGGER.info("News articles fetched successfully. External api call took {} ms", duration);
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

    /**
     * This method fetches the users saved preferences and makes an api call to the news api with the saved preferences or the defaults.
     * @return
     */
    public NewsArticlesResponse fetchNewsBasedOnPreferences() {
        LOGGER.info("Fetch news based on saved news-preferences");
        long startTime = System.currentTimeMillis();
        User currentUser = aggregatorService.getCurrentUser();
        Preferences preferences = preferenceRepository.findByUser(currentUser).orElse(new Preferences());

        String query = "";
        if (!CollectionUtils.isEmpty(preferences.getTopics())) {
            query = String.join(" AND ", preferences.getTopics());
        }

        String region = "";
        if (!CollectionUtils.isEmpty(preferences.getRegion())) {
            region = String.join(",", preferences.getRegion());
        }

        String language = "";
        if (!CollectionUtils.isEmpty(preferences.getRegion())) {
            language = String.join(",", preferences.getLanguage());
        }

        String url = newsApiProperties.getBaseUrl() + "/" + "everything";
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(QUERY_STRING, query);
        queryParams.put(QUERY_REGION, region);
        queryParams.put(QUERY_LANGUAGE, language);
        queryParams.put(API_KEY, newsApiProperties.getApiKey());
        queryParams.put(PAGE_SIZE, DEFAULT_PAGE_SIZE);
        queryParams.put(PAGE, "1");

        queryParams.forEach(builder::queryParam);

        URI uri = builder.build().encode().toUri();

        try {
            NewsArticlesResponse newsArticlesResponse = restTemplate.getForObject(uri, NewsArticlesResponse.class);
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            LOGGER.info("News response retrieved successfully. External api call took : {} ms", duration);

            return newsArticlesResponse;
        }
        catch (HttpClientErrorException | HttpServerErrorException ex) {
            LOGGER.error("Error occurred while calling news-api : {}", ex.getMessage());
            throw ex;
        }
        catch (Exception ex) {
            LOGGER.error("Generalised Error occurred while calling news-api : {} ", ex.getMessage());
            throw ex;
        }
    }

    /**
     * Fetch all supported languages in ISO format
     * @return
     */
    public List<String> getSupportedLanguages() {
        return Arrays.stream(SupportedLanguages.values()).map(lang -> lang.getCode() + " : " + lang.getLanguage()).collect(Collectors.toList());
    }

    /**
     * Fetch all countries from Java Locale ISO api
     * @return
     */
    public List<String> getCountries() {
        List<String> countryCodes = new ArrayList<>();
        for(String code : Locale.getISOCountries()) {
            Locale locale = new Locale("", code);
            String countryName = locale.getDisplayName();
            countryCodes.add(code + " => " + countryName);
        }
        return countryCodes;
    }
}
