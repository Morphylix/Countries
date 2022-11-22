package com.morphylix.android.countries.domain.model.network

import com.google.gson.annotations.SerializedName

data class CountryNetworkEntity(
    val name: String = "",
    val capital: String = "",
    @SerializedName("numericCode") val cnn3: String = ""
)
