package com.example.dal

interface DataContextFactory {
    /*
    Readonly context
     */
    fun createReadOnly(): DataContext

    /*
    Read-write context
     */
    fun create(): DataContext
}
