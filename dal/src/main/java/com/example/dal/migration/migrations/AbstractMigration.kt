package com.example.dal.migration.migrations

import com.example.dal.DataContext
import com.example.dal.DataContextException

abstract class AbstractMigration {
    protected lateinit var dataContext: DataContext

    fun setDatabase(dataContext: DataContext) {
        this.dataContext = dataContext
    }

    @Throws(DataContextException::class)
    abstract fun up()
}
