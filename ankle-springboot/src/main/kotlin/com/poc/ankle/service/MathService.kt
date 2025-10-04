package com.poc.ankle.service

import com.poc.air.service.CalculatorService
import org.springframework.stereotype.Service

@Service
class MathService(
    private val calculatorService: CalculatorService
) {

    suspend fun doPlus(): String {
        return calculatorService.plus(100, 100).value.toString()
    }

    suspend fun doMinus(): String {
        return calculatorService.minus(100, 100).toString()
    }
}
