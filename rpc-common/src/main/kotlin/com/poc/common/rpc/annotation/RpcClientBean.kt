package com.poc.common.rpc.annotation

import org.springframework.context.annotation.Bean
import org.springframework.core.annotation.AliasFor

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Bean
annotation class RpcClientBean(
    @get:AliasFor(annotation = Bean::class, attribute = "name")
    val id: String
)
