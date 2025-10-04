package com.poc.common.rpc.proxy

import com.poc.common.rpc.exception.RpcException
import com.poc.common.rpc.model.RpcConfig
import com.poc.common.rpc.model.RpcRequestPayload
import com.poc.common.rpc.model.RpcResponsePayload
import com.fasterxml.jackson.databind.ObjectMapper
import com.poc.common.rpc.constant.RPC_HEADER
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.time.Duration
import java.util.concurrent.TimeoutException
import kotlin.coroutines.Continuation
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.kotlinFunction

/**
 * Class responsible for handling dynamic method invocations for RPC (Remote Procedure Call) proxy objects.
 *
 * This class implements the `InvocationHandler` interface to intercept and manage method calls on
 * proxy instances. It facilitates remote method calls to an RPC service by:
 * - Dynamically constructing the request payload for remote calls.
 * - Executing HTTP POST requests to the configured RPC service endpoint.
 * - Handling server responses and mapping them to the return type of the target method.
 * - Supporting both synchronous and suspend methods through method signature inspection.
 *
 * Key Characteristics:
 * - `id`: Identifier for the RPC client handler.
 * - `path`: The RPC endpoint path; defaults to `/rpc/{id}` if left blank.
 * - `config`: Instance of `RpcConfig` holding host and timeout configuration.
 *
 * Runtime Behavior:
 * - Intercepts calls to methods on a proxy object.
 * - Constructs an `RpcRequestPayload` with the method name and arguments.
 * - Sends the request as an HTTP POST to the RPC service.
 * - Maps the remote call response to the expected return type or throws `RpcException` on error.
 * - Logs details of errors or timeouts for debugging purposes.
 *
 * Thread safety considerations depend on the underlying implementation of `WebClient`, which is used for HTTP requests.
 */
open class RpcClientProxyInvocationHandler(
    private val id: String,
    private val path: String,
    private val config: RpcConfig
): InvocationHandler {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val objectMapper = ObjectMapper()
    private val webClient = WebClient.create(config.host)
    private val servletUrl = path.takeIf { it.isNotBlank() } ?: "/rpc/$id"

    /**
     * Invokes a specified method dynamically on a proxy instance, handling RPC operations such as
     * constructing payloads, sending HTTP requests, and processing server responses. Supports both
     * synchronous and suspend methods.
     *
     * @param proxy the proxy object on which the method is invoked, or null if no proxy is used
     * @param method the specific method to be invoked
     * @param args the arguments passed to the method; can include a continuation object for suspend functions
     * @return the result of the method invocation converted to the appropriate return type
     * @throws RpcException if an error occurs in the RPC operation, such as timeout, parsing errors, or remote-side failures
     */
    override fun invoke(
        proxy: Any?,
        method: Method,
        args: Array<out Any>?
    ): Any {
        // construct params
        val isSuspend = args.continuation() != null
        val params = if (isSuspend) {
            args?.toList()?.take(args.size - 1) as List<Any>
        } else {
            args?.toList() as List<Any>
        }

        // construct payload
        val requestPayload = RpcRequestPayload(
            methodName = method.name,
            parameters = params
        )

        try {
            // call http
            val response = webClient.post()
                .uri(servletUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header(RPC_HEADER, id)
                .bodyValue(requestPayload)
                .retrieve()
                .bodyToMono(RpcResponsePayload::class.java)
                .timeout(Duration.ofMillis(config.timeoutMs))
                .block()!!

            // propagate errors if success is false
            if (!response.success) {
                throw RpcException(response.message)
            }

            // convert to method return type
            val javaType = objectMapper.typeFactory.constructType(method.kotlinFunction?.returnType?.javaType)
            return objectMapper.convertValue(response.data, javaType)

        } catch (e: TimeoutException) {
            val message = "Request timed out after ${config.timeoutMs} ms. id: $id. url=$servletUrl. method: ${method.name}"
            logger.error("[RpcClientProxy] $message", e)
            throw RpcException(message)

        } catch (e: Exception) {
            if (e is RpcException) throw e
            logger.error("An error occurred: ${e.message}", e)
            throw RpcException(e.message)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun Array<*>?.continuation(): Continuation<Any?>? {
        return this?.lastOrNull() as? Continuation<Any?>
    }
}