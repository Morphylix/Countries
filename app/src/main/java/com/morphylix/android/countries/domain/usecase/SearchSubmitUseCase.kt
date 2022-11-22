package com.morphylix.android.countries.domain.usecase

import com.morphylix.android.countries.CountriesApp
import com.morphylix.android.countries.domain.model.domain.Country

class SearchSubmitUseCase {

    private var countriesRepository = CountriesApp.getInstance().countriesRepository

    suspend fun execute(name: String): Country {
        return countriesRepository.searchCountries(name)[0]
    }
}