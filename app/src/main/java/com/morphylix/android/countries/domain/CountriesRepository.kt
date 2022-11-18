package com.morphylix.android.countries.domain

import com.morphylix.android.countries.domain.model.domain.Country

interface CountriesRepository {

    suspend fun fetchCountryFlag()

    suspend fun fetchCountryPopulation()

    suspend fun searchCountries(name: String): List<Country>

}