package com.poc.common.rpc.configuration

import com.poc.common.rpc.factory.RpcClientProxyFactory
import com.poc.common.rpc.processor.RpcClientBeanPostProcessor
import com.poc.common.rpc.processor.RpcHostBeanPostProcessor
import com.poc.common.rpc.properties.RpcConfigurationProperties
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Lazy
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

/**
 * Auto-configuration class for setting up the RPC (Remote Procedure Call) framework within a Spring application.
 *
 * This class is annotated with `@AutoConfiguration` to allow Spring Boot to automatically include
 * it when certain conditions are met. It integrates with Spring's auto-configuration mechanism to
 * register and manage RPC-related beans and configurations.
 *
 * Primary Responsibilities:
 * - Defines and manages key components related to the RPC framework.
 * - Enables and binds `RpcConfigurationProperties` to provide dynamic configuration of RPC client proxies.
 * - Configures BeanPostProcessors for automatic handling of RPC server and client components.
 *
 * Key Annotations:
 * - `@AutoConfiguration`: Marks this class as a candidate for Spring Boot's auto-configuration feature.
 * - `@ConditionalOnClass(RequestMappingHandlerMapping::class)`: Ensures the configuration is loaded only
 *    if `RequestMappingHandlerMapping` is present in the classpath, making it specific to web-based applications.
 * - `@EnableConfigurationProperties(RpcConfigurationProperties::class)`: Binds the RPC configuration
 *    properties to a centralized configuration structure (`RpcConfigurationProperties`).
 *
 * Bean Definitions:
 * - `rpcHostBeanPostProcessor`: A `BeanPostProcessor` for automatically registering RPC server endpoints
 *   annotated with `@RpcServlet`. Works by integrating with Spring's `RequestMappingHandlerMapping`.
 * - `rpcClientBeanPostProcessor`: A `BeanPostProcessor` for managing RPC client proxies.
 * - `rpcClientBuilderService`: A factory service for dynamically creating RPC client proxies using
 *   the provided configurations (`RpcConfigurationProperties`).
 *
 * Conditions for Activation:
 * - `RequestMappingHandlerMapping` class must be available in the application's classpath.
 * - The configuration properties defined in `RpcConfigurationProperties` can be provided using
 *   standard application configuration files (e.g., `application.yml` or `application.properties`).
 *
 * Integration Points:
 * - Dynamic registration of HTTP endpoints for managing server-side RPC services.
 * - Centralized management of RPC client settings and proxy creation.
 */
@AutoConfiguration
@ConditionalOnClass(RequestMappingHandlerMapping::class)
@EnableConfigurationProperties(RpcConfigurationProperties::class)
class RpcAutoConfiguration {

    companion object {

        @Bean
        @Lazy
        fun rpcHostBeanPostProcessor(
            @Lazy requestMappingHandlerMapping: RequestMappingHandlerMapping
        ) = RpcHostBeanPostProcessor(requestMappingHandlerMapping)

        @Bean
        @Lazy
        fun rpcClientBeanPostProcessor() = RpcClientBeanPostProcessor()
    }

    @Bean
    fun rpcClientBuilderService(
        rpcConfigurationProperties: RpcConfigurationProperties
    ): RpcClientProxyFactory {
        return RpcClientProxyFactory(rpcConfigurationProperties)
    }
}