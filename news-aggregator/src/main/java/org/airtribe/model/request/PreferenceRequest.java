package org.airtribe.model.request;

import java.util.List;

public class PreferenceRequest {

    List<String> preferences;

    List<String> languages;

    List<String> regions;

    public List<String> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<String> preferences) {
        this.preferences = preferences;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<String> getRegions() {
        return regions;
    }

    public void setRegions(List<String> regions) {
        this.regions = regions;
    }

    @Override
    public String toString() {
        return "PreferenceRequest{" +
                "preferences=" + preferences +
                ", languages=" + languages +
                ", regions=" + regions +
                '}';
    }
}
