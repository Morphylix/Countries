package com.morphylix.android.countries.domain.model.network

import com.morphylix.android.countries.domain.model.domain.Country
import com.morphylix.android.countries.util.EntityMapper

class NetworkEntityMapper : EntityMapper<Country, CountryNetworkEntity> {
    override fun mapToEntity(obj: Country): CountryNetworkEntity {
        TODO("Not yet implemented")
    }

    override fun mapFromEntity(entity: CountryNetworkEntity) = Country(
        name = entity.name,
        capital = entity.capital,
        cnn3 = entity.cnn3
    )

    fun mapFromEntityList(entityList: List<CountryNetworkEntity>): List<Country> {
        return entityList.map {
            mapFromEntity(it)
        }
    }
}