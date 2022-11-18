package com.morphylix.android.countries.data.network

import com.morphylix.android.countries.domain.model.network.CountryNetworkEntity
import retrofit2.http.GET
import retrofit2.http.Path

interface SearchCountriesApi {

    @GET("v2/name/{name}")
    suspend fun searchCountries(@Path("name") name: String): List<CountryNetworkEntity>
}