package com.example.dal

interface DataContext : AutoCloseable {
    fun executeCursor(sql: String, vararg arguments: Any): DataCursor
    @Throws(DataContextException::class)
    fun exists(sql: String, vararg arguments: Any): Boolean

    @Throws(DataContextException::class)
    fun executeInt(sql: String, vararg arguments: Any): Int?

    @Throws(DataContextException::class)
    fun executeLong(sql: String, vararg arguments: Any): Long?

    @Throws(DataContextException::class)
    fun executeString(sql: String, vararg arguments: Any): String

    @Throws(DataContextException::class)
    fun executeBlob(sql: String, vararg arguments: Any): ByteArray

    fun executeSql(sql: String)
    fun executeSql(sql: String, parameters: Array<String>)
    fun update(table: String, values: DataContentValues, whereClause: String, vararg whereArgs: Any): Int
    fun delete(table: String, whereClause: String, vararg whereArgs: Any): Int
    fun insert(table: String, values: DataContentValues): Long
    fun insertOrReplace(table: String, values: DataContentValues): Long

    fun beginTransaction()
    fun commitTransaction()
}

