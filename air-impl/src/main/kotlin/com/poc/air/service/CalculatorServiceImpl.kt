package com.poc.air.service

import com.poc.air.data.PlusResult
import com.poc.common.rpc.annotation.RpcServlet
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
@RpcServlet("calculator")
class CalculatorServiceImpl: CalculatorService {
    private val logger = LoggerFactory.getLogger(this::class.java)

    init {
        logger.info("start!!!")
    }

    override suspend fun plus(a: Int, b: Int): PlusResult {
        logger.info("[CalculatorService] plusssss $a $b")
        return PlusResult(a + b)
    }

    override fun minus(a: Int, b: Int): Int {
        logger.info("[CalculatorService] minusss $a $b")
        return a - b
    }
}