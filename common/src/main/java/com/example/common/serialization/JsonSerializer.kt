package com.example.common.serialization

interface JsonSerializer {
    fun <T> fromJson(json: String, classOfT: Class<T>): T
    fun toJson(value: Any): String
}
