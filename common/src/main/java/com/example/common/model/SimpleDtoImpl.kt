package com.example.common.model

import com.example.common.serialization.JsonSerializer

class SimpleDtoImpl(override var id: Long,
                    override val objectType: ObjectType,
                    override val parentId: Long?,
                    override val name: String,
                    override val description: String) : SimpleDto {

    fun toJson(jsonSerializer: JsonSerializer): String {
        return jsonSerializer.toJson(this)
    }
}