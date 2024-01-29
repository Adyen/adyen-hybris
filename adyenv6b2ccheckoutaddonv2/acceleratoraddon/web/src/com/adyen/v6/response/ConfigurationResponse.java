package com.adyen.v6.response;

import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.TitleData;

import java.util.List;

public class ConfigurationResponse {
    private boolean isAnonymous;
    private List<CountryData> countries;
    private List<TitleData> titles;

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    public List<CountryData> getCountries() {
        return countries;
    }

    public void setCountries(List<CountryData> countries) {
        this.countries = countries;
    }

    public List<TitleData> getTitles() {
        return titles;
    }

    public void setTitles(List<TitleData> titles) {
        this.titles = titles;
    }
}
