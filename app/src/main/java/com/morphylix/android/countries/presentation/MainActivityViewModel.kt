package com.morphylix.android.countries.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morphylix.android.countries.domain.usecase.SearchCountriesUseCase
import com.morphylix.android.countries.domain.usecase.SearchSubmitUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val TAG = "MainActivityViewModel"

class MainActivityViewModel : ViewModel() {

    private var _mainActivityState =
        MutableStateFlow<MainActivityState>(MainActivityState.Loading)
    val mainActivityState: StateFlow<MainActivityState>
        get() = _mainActivityState
    private val searchCountriesUseCase = SearchCountriesUseCase()
    private val searchSubmitUseCase = SearchSubmitUseCase()

    fun searchCountries(name: String) {
        viewModelScope.launch {
            val suggestions = searchCountriesUseCase.execute(name)

            _mainActivityState.value =
                MainActivityState.SuggestionsSuccess(suggestions)
            Log.i(TAG, "got ${suggestions.size}")
        }
    }

    fun getCapital(name: String) {
        viewModelScope.launch {
            val country = searchCountriesUseCase.execute(name)
            _mainActivityState.value = MainActivityState.CapitalSuccess(country[0].capital)
        }
    }

    fun getCnn3(name: String) {
        viewModelScope.launch {
            val country = searchSubmitUseCase.execute(name)
            _mainActivityState.value = MainActivityState.Cnn3Success(country.cnn3)
        }
    }

    fun synchronize() {
        _mainActivityState.value = MainActivityState.Synchronize
    }

    fun setLoadingState() {
        _mainActivityState.value = MainActivityState.Loading
    }

}