package com.poc.ankle.controller

import com.poc.ankle.service.MathService
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AnkleController(
    private val mathService: MathService
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/execute")
    fun execute(): String {
        logger.info("[AnkleController] doPlus!")
        val res1 = runBlocking { mathService.doPlus() }
        logger.info("[AnkleController] doPlus done! res=$res1")

        logger.info("[AnkleController] doMinus!")
        val res2 = runBlocking { mathService.doMinus() }
        logger.info("[AnkleController] doMinus done! res=$res2")

        return "success"
    }
}