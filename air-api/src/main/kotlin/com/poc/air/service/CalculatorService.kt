package com.poc.air.service

import com.poc.air.data.PlusResult

interface CalculatorService {
    suspend fun plus(a: Int, b: Int): PlusResult
    fun minus(a: Int, b: Int): Int
}