package com.poc.air.application

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["com.poc.air"])
class PocAirBackendApplication

fun main(args: Array<String>) {
	runApplication<PocAirBackendApplication>(*args)
}
