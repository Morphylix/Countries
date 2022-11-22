package com.morphylix.android.countries.presentation

import com.morphylix.android.countries.domain.model.domain.Country

sealed class MainActivityState {

    object Loading: MainActivityState()

    class SuggestionsSuccess(val suggestions: List<Country>): MainActivityState()

    class CapitalSuccess(val capital: String): MainActivityState()

    class Cnn3Success(val cnn3: String): MainActivityState()

    object Synchronize: MainActivityState()

    class Error(e: Exception): MainActivityState()

}
