package com.poc.ankle.application

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["com.poc.ankle"])
class PocAnkleBackendApplication

fun main(args: Array<String>) {
	runApplication<PocAnkleBackendApplication>(*args)
}
