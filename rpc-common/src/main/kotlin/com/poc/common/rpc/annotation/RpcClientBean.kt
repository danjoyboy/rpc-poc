package com.poc.common.rpc.annotation

import org.springframework.context.annotation.Bean
import org.springframework.core.annotation.AliasFor

/**
 * Annotation used to designate a bean as an RPC (Remote Procedure Call) client.
 * Annotating a method with `@RpcClientBean` enables its registration in the
 * Spring application context with a specified identifier.
 *
 * This annotation is primarily processed by the `RpcClientBeanPostProcessor`, which ensures that
 * RPC client beans are uniquely registered and made available for dependency injection.
 *
 * Annotated methods must specify an `id` parameter, which serves as the unique identifier
 * for the client. If a method with a duplicate `id` is detected, a processing exception will be raised.
 *
 * Primary use cases include integrating remote procedure calls within a distributed system or
 * microservice-based application, facilitating communication between services.
 *
 * @property id The unique identifier for the RPC client bean. This value is mandatory and is
 *              used to register and distinguish the RPC client bean within the application context.
 * @see com.poc.common.rpc.annotation.RpcClient
 * @see com.poc.common.rpc.processor.RpcClientBeanPostProcessor
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Bean
annotation class RpcClientBean(
    @get:AliasFor(annotation = Bean::class, attribute = "name")
    val id: String
)
