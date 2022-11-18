package com.morphylix.android.countries.data

import android.util.Log
import com.morphylix.android.countries.data.network.SearchCountriesApi
import com.morphylix.android.countries.domain.CountriesRepository
import com.morphylix.android.countries.domain.model.domain.Country
import com.morphylix.android.countries.domain.model.network.CountryNetworkEntity
import com.morphylix.android.countries.domain.model.network.NetworkEntityMapper
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CountriesRepositoryImpl: CountriesRepository {

    private val networkEntityMapper = NetworkEntityMapper()

    private val client: OkHttpClient = OkHttpClient.Builder()
        .build()
    private val searchCountriesRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://restcountries.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    private val searchCountriesApi = searchCountriesRetrofit.create(SearchCountriesApi::class.java)

    override suspend fun fetchCountryFlag() {
        TODO("Not yet implemented")
    }

    override suspend fun fetchCountryPopulation() {
        TODO("Not yet implemented")
    }

    override suspend fun searchCountries(name: String): List<Country> {
        var countries = listOf<CountryNetworkEntity>()
        try {
            countries = searchCountriesApi.searchCountries(name)
        } catch(e: Exception) {
            Log.i("TEST", "TEST")
        }
        return networkEntityMapper.mapFromEntityList(countries)
    }
}