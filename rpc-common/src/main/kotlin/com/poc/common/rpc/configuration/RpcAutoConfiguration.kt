package com.poc.common.rpc.configuration

import com.poc.common.rpc.builder.RpcClientProxyFactory
import com.poc.common.rpc.processor.RpcClientBeanPostProcessor
import com.poc.common.rpc.processor.RpcHostBeanPostProcessor
import com.poc.common.rpc.properties.RpcConfigurationProperties
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Lazy
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

@AutoConfiguration
@ConditionalOnClass(RequestMappingHandlerMapping::class)
@EnableConfigurationProperties(RpcConfigurationProperties::class)
class RpcAutoConfiguration {

    private val logger = LoggerFactory.getLogger(this::class.java)

    init {
        logger.info("[RpcAutoConfiguration] Loading RPC auto-configuration")
    }

    companion object {

        @Bean
        @Lazy
        fun rpcHostBeanPostProcessor(
            @Lazy requestMappingHandlerMapping: RequestMappingHandlerMapping
        ) = RpcHostBeanPostProcessor(requestMappingHandlerMapping).also {
            LoggerFactory.getLogger(RpcAutoConfiguration::class.java).info("rpc host bean initialized")
        }

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