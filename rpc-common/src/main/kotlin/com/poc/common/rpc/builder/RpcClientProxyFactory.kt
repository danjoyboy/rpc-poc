package com.poc.common.rpc.builder

import com.poc.common.rpc.exception.RpcException
import com.poc.common.rpc.properties.RpcConfigurationProperties
import com.poc.common.rpc.proxy.RpcClientProxyInvocationHandler
import java.lang.reflect.Proxy

class RpcClientProxyFactory(
    val rpcConfigurationProperties: RpcConfigurationProperties
) {

    inline fun <reified T> createProxy(id: String): T {
        val config = rpcConfigurationProperties[id]
            ?: throw RpcException("No configuration available for `$id` rpc in application property file")
        return Proxy.newProxyInstance(
            T::class.java.classLoader,
            arrayOf(T::class.java),
            RpcClientProxyInvocationHandler(id, config)
        ) as T
    }
}