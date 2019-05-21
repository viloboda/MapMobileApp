package com.example.dal.migration.migrations

import com.example.common.TableNames
import com.example.common.model.SimpleDto
import com.example.common.model.SimpleDtoImpl
import com.example.common.serialization.GsonSerializer
import com.example.dal.DataContentValues

class DBVersion1 : AbstractMigration() {

    override fun up() {
        dataContext.executeSql("CREATE TABLE ${TableNames.METADATA} (custom_version INTEGER NOT NULL)")
        val metadata = DataContentValues(1)
        metadata.put("custom_version", AbstractMigration.CUSTOM_USER_VERSION)
        dataContext.insert(TableNames.METADATA, metadata)

        dataContext.executeSql("CREATE TABLE ${TableNames.GEO_DATA} " +
                "( id INTEGER NOT NULL, type INTEGER NOT NULL, attributes TEXT NULL, PRIMARY KEY ( id ) )")
        dataContext.executeSql("CREATE INDEX idx_${TableNames.GEO_DATA}_type ON ${TableNames.GEO_DATA}(type)")

        val serializer = GsonSerializer()
    }

    private fun saveObject(objectDto: SimpleDtoImpl, serializer: GsonSerializer) {
        val parameters = DataContentValues()
        parameters.put("id", objectDto.id)
        parameters.put("type", objectDto.objectType.id)
        //parameters.put("attributes", objectDto.toJson(serializer))
        dataContext.insert(TableNames.GEO_DATA, parameters)
    }

    override fun down() {

    }
}

