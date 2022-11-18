package com.morphylix.android.countries.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morphylix.android.countries.domain.model.network.NetworkEntityMapper
import com.morphylix.android.countries.domain.usecase.SearchCountriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val TAG = "MainActivityViewModel"

class MainActivityViewModel : ViewModel() {

    private var _suggestionsState =
        MutableStateFlow<MainActivitySuggestionsState>(MainActivitySuggestionsState.Loading)
    val suggestionsState: StateFlow<MainActivitySuggestionsState>
        get() = _suggestionsState
    private val searchCountriesUseCase = SearchCountriesUseCase()

    fun searchCountries(name: String) {
        viewModelScope.launch {
            val suggestions = searchCountriesUseCase.execute(name)

            _suggestionsState.value =
                MainActivitySuggestionsState.SuggestionsSuccess(suggestions)
            Log.i(TAG, "got ${suggestions.size}")
        }
    }

    fun getCapital(name: String) {
        viewModelScope.launch {
            val country = searchCountriesUseCase.execute(name)
            _suggestionsState.value = MainActivitySuggestionsState.CapitalSuccess(country[0].capital)
        }
    }

    fun setLoadingState() {
        _suggestionsState.value = MainActivitySuggestionsState.Loading
    }

}