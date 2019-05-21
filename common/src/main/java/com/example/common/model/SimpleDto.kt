package com.example.common.model


interface SimpleDto {
    var id: Long

    val objectType: ObjectType

    val parentId: Long?

    val name: String

    val description: String
}