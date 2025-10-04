package com.poc.common.rpc.factory

import com.poc.common.rpc.exception.RpcException
import com.poc.common.rpc.properties.RpcConfigurationProperties
import com.poc.common.rpc.proxy.RpcClientProxyInvocationHandler
import java.lang.reflect.Proxy

/**
 * Factory class for creating dynamic RPC client proxies.
 *
 * This class facilitates the creation of dynamic proxy instances for interfaces, enabling
 * interaction with remote services using RPC (Remote Procedure Calls) based on the configurations
 * provided in the application properties file. It uses the RPC service configuration to dynamically
 * generate proxy implementations for the specified interfaces.
 *
 * Primary Responsibilities:
 * - Provide a centralized mechanism to create proxies for RPC services.
 * - Use configurations present in `RpcConfigurationProperties` to identify service parameters
 *   such as host and timeout settings.
 * - Throw an exception if configuration for the requested RPC service identifier is not available.
 *
 * @constructor
 * Initializes the factory with the given RPC configuration properties.
 *
 * @property rpcConfigurationProperties Contains mappings of RPC service identifiers to their corresponding
 * configurations, including the service's host and timeout settings.
 */
class RpcClientProxyFactory(
    val rpcConfigurationProperties: RpcConfigurationProperties
) {

    /**
     * Creates a dynamic proxy instance for the specified interface type using the provided RPC client configuration.
     *
     * @param id the identifier of the RPC client configuration to use; must match a key present in the configuration properties
     * @param path the optional RPC endpoint path; defaults to an empty string, which results in the default path being used
     * @return a proxy instance implementing the specified interface type T, providing remote calls to the RPC service
     * @throws RpcException if no configuration is found for the specified id or if there is a failure in creating the proxy
     */
    inline fun <reified T> createProxy(
        id: String,
        path: String = ""
    ): T {
        val config = rpcConfigurationProperties[id]
            ?: throw RpcException("No configuration available for `$id` rpc in application property file")
        return Proxy.newProxyInstance(
            T::class.java.classLoader,
            arrayOf(T::class.java),
            RpcClientProxyInvocationHandler(id, path, config)
        ) as T
    }
}