package com.morphylix.android.countries.domain.usecase

import com.morphylix.android.countries.CountriesApp
import com.morphylix.android.countries.domain.model.domain.Country

class SearchCountriesUseCase {

    private var countriesRepository = CountriesApp.getInstance().countriesRepository

    suspend fun execute(name: String): List<Country> {
        return countriesRepository.searchCountries(name)
    }
}