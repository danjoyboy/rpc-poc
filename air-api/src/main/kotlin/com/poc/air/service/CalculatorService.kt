package com.poc.air.service

interface CalculatorService {
    suspend fun plus(a: Int, b: Int): PlusResult
    fun minus(a: Int, b: Int): Int
}

data class PlusResult(
    val value: Int = 0
)