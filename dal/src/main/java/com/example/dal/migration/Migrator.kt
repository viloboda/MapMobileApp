package com.example.dal.migration

import com.example.dal.*
import com.example.dal.migration.migrations.AbstractMigration

internal class Migrator(private val packageName: String) {

    @Throws(MigrationException::class)
    fun upgrade(db: DataContext, oldVersion: Int, newVersion: Int) {
        for (x in oldVersion + 1..newVersion) {
            try {
                db.beginTransaction()

                handleUp(db, "DBVersion$x")

                val params = DataContentValues(1)
                params.put("id", x)
                db.insert("version", params)

                db.commitTransaction()
            } catch (e: Exception) {
                throw MigrationException(e)
            }

        }
    }

    @Throws(InstantiationException::class, IllegalAccessException::class, ClassNotFoundException::class)
    private fun getMigrationForPackageAndName(db: DataContext, pckName: String, clzzName: String): AbstractMigration {
        val migration = Class.forName("$pckName.$clzzName").newInstance() as AbstractMigration
        migration.setDatabase(db)
        return migration
    }

    @Throws(InstantiationException::class, IllegalAccessException::class, ClassNotFoundException::class, DataContextException::class)
    private fun handleUp(db: DataContext, clzzName: String) {
        val migration = getMigrationForPackageAndName(db, packageName, clzzName)
        migration.up()
    }
}