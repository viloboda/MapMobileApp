package com.example.dal

class DataContextException : Exception {
    constructor(e: Exception) : super(e)

    constructor(message: String) : super(message)

    constructor(message: String, ex: Exception) : super(message, ex)
}

