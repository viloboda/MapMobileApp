package com.example.dal.migration

import com.example.dal.DataContext
import com.example.dal.DataContextException
import com.example.dal.MigrationException

class MigrationsDatabaseHelper(private val dbVersion: Int, packageName: String) {
    private val migrator: Migrator = Migrator(packageName)

    @Throws(MigrationException::class)
    fun migrate(dataContext: DataContext) {
        try {

            var currentVersion = 0
            if (!dataContext.exists("SELECT 1 FROM sqlite_master WHERE type='table' AND name='version';")) {
                dataContext.executeSql("CREATE TABLE version (id INTEGER PRIMARY KEY)")
            }

            currentVersion = dataContext.executeInt("SELECT max(id) as id FROM version")!!

            migrator.upgrade(dataContext, currentVersion, dbVersion)
        } catch (e: DataContextException) {
            e.printStackTrace()
        }

    }
}