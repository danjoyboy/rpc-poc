package com.poc.common.rpc.proxy

import com.poc.common.rpc.exception.RpcException
import com.poc.common.rpc.model.RpcConfig
import com.poc.common.rpc.model.RpcRequestPayload
import com.poc.common.rpc.model.RpcResponsePayload
import com.fasterxml.jackson.databind.ObjectMapper
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

open class RpcClientProxyInvocationHandler(
    private val id: String,
    private val config: RpcConfig
): InvocationHandler {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val objectMapper = ObjectMapper()
    private val webClient = WebClient.create(config.host)

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
                .uri("/rpc/$id")
                .contentType(MediaType.APPLICATION_JSON)
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
            val message = "Request timed out after ${config.timeoutMs} ms. id: $id. method: ${method.name}"
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