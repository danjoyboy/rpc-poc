package com.poc.ankle.configuration

import com.poc.air.service.CalculatorService
import com.poc.common.rpc.factory.RpcClientProxyFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RpcClientConfiguration(
    private val rpcClientProxyFactory: RpcClientProxyFactory
) {

    @Bean
    fun calculator(): CalculatorService {
        return rpcClientProxyFactory.createProxy("calculator")
    }
}

