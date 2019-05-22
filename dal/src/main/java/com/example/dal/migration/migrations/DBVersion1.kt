package com.example.dal.migration.migrations

import com.example.dal.TableNames
import com.example.dal.DataContentValues
import model.GeoData
import org.locationtech.jts.geom.Dimension

class DBVersion1 : AbstractMigration() {

    override fun up() {
        dataContext.executeSql("CREATE TABLE ${TableNames.GEO_DATA} " +
                "( id INTEGER NOT NULL, geometry TEXT, type INTEGER NOT NULL, attributes TEXT NULL, PRIMARY KEY ( id ) )")
        dataContext.executeSql("CREATE INDEX idx_${TableNames.GEO_DATA}_type ON ${TableNames.GEO_DATA}(type)")

        dataContext.executeSql("CREATE VIRTUAL TABLE  ${TableNames.GEO_INDEX} USING rtree( id, minX, maxX, minY, maxY )")
    }

    private fun saveObject(objectDto: GeoData) {
        val parameters = DataContentValues()
        parameters.put("id", objectDto.id)
        parameters.put("geometry", objectDto.geometry.toText())
        parameters.put("type", objectDto.objectType.id)
        parameters.put("attributes", objectDto.attributes)
        dataContext.insert(TableNames.GEO_DATA, parameters)

        val env = if (objectDto.geometry.dimension == Dimension.P || objectDto.geometry.dimension == Dimension.L)
            objectDto.geometry.buffer(0.0002).envelopeInternal
        else objectDto.geometry.envelopeInternal

        val indexData = DataContentValues(5)
        indexData.put("id", objectDto.id)
        indexData.put("minX", env.minX)
        indexData.put("minY", env.minY)
        indexData.put("maxX", env.maxX)
        indexData.put("maxY", env.maxY)
        dataContext.insertOrReplace(TableNames.GEO_INDEX, indexData)
    }
}

