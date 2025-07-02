package org.airtribe.controller;

import org.airtribe.model.NewsArticlesResponse;
import org.airtribe.model.Preferences;
import org.airtribe.model.request.NewsSearchRequest;
import org.airtribe.model.request.PreferenceRequest;
import org.airtribe.service.AggregatorService;
import org.airtribe.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;

@RestController
@RequestMapping("api/aggregator")
public class AggregatorController {

    @Autowired
    AggregatorService aggregatorService;

    @Autowired
    NewsService newsService;

    @PutMapping("/preferences")
    public Preferences savePreferences(PreferenceRequest preferenceRequest) {
        return aggregatorService.updatePreferences(preferenceRequest);
    }

    @GetMapping("/preferences")
    public Preferences getPreferences() {
        return aggregatorService.getPreferences();
    }

    @PostMapping("/news")
    public NewsArticlesResponse fetchNews(@RequestBody NewsSearchRequest request) {
        return newsService.fetchNewsArticles(request);
    }

    @GetMapping("/supportedLanguages")
    public List<String> getSupportedLanguages() {
        return newsService.getSupportedLanguages();
    }


    @ExceptionHandler({HttpClientErrorException.class, HttpServerErrorException.class})
    public String handleHttpExceptions(RuntimeException ex) {
        return "External api error : " + ex.getMessage();
    }
}
