package com.morphylix.android.countries

import android.app.Application
import com.morphylix.android.countries.data.CountriesRepositoryImpl

class CountriesApp : Application() {
    val countriesRepository = CountriesRepositoryImpl()

    companion object {
        private var INSTANCE: CountriesApp? = null
        fun getInstance(): CountriesApp {
            if (INSTANCE == null) {
                INSTANCE = CountriesApp()
            }
            return INSTANCE as CountriesApp
        }
    }
}