package com.morphylix.android.countries.presentation

import com.morphylix.android.countries.domain.model.domain.Country

sealed class MainActivitySuggestionsState {

    object Loading: MainActivitySuggestionsState()

    class SuggestionsSuccess(val suggestions: List<Country>): MainActivitySuggestionsState()

    class CapitalSuccess(val capital: String): MainActivitySuggestionsState()

    class Cnn3Success(val cnn3: String): MainActivitySuggestionsState()

    class Error(e: Exception): MainActivitySuggestionsState()

}
