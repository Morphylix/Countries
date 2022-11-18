package com.morphylix.android.countries.util

interface EntityMapper<Object, Entity> {

    fun mapToEntity(obj: Object): Entity

    fun mapFromEntity(entity: Entity): Object

}