package com.example.dal

class MigrationException : Exception {

    constructor()

    constructor(arg0: String) : super(arg0)

    constructor(arg0: Throwable) : super(arg0)

    constructor(arg0: String, arg1: Throwable) : super(arg0, arg1)

}
