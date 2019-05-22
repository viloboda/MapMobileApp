package com.example.dal

import org.locationtech.jts.io.WKTReader

import model.GeoData
import model.ObjectType

class ObjectDataCursor(cursor: DataCursor) : DataCursorAbstract<GeoData>(cursor) {

    private val wktReader = WKTReader()

    @Throws(DataContextException::class)
    override fun createObject(cursor: DataCursor): GeoData {

        try {
            val objectType = ObjectType.objectTypes[cursor.getInt("type")]

            return GeoData(cursor.getLong("id"),
                    wktReader.read(cursor.getString("geometry")!!),
                    objectType!!,
                    cursor.getString("attributes")
            )
        } catch (ex: Exception) {
            throw DataContextException(ex)
        }

    }
}

