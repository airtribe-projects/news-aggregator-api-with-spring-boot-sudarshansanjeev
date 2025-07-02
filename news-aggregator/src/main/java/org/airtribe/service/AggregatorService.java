package org.airtribe.service;

import org.airtribe.model.Preferences;
import org.airtribe.model.request.PreferenceRequest;
import org.airtribe.repository.PreferenceRepository;
import org.airtribe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.airtribe.model.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Service
public class AggregatorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AggregatorService.class);
    @Autowired
    UserRepository userRepository;

    @Autowired
    PreferenceRepository preferenceRepository;

    public Preferences updatePreferences(PreferenceRequest request) {
        User user = getCurrentUser();
        LOGGER.info("Update preferences : {}" , request);
        LOGGER.info("User : {}", user.getUsername() );
        Preferences userPreference = preferenceRepository.findByUser(user).orElse(new Preferences());
        userPreference.setUser(user);
        userPreference.setTopics(request.getPreferences());
        userPreference.setRegion(request.getRegions());
        userPreference.setLanguage(request.getLanguages());
        LOGGER.info("Updated user preferences : {}", userPreference);
        return preferenceRepository.save(userPreference);
    }

    public Preferences getPreferences() {
        User user = getCurrentUser();
        return preferenceRepository.findByUser(user).orElse(null);
    }

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow();
    }
}
