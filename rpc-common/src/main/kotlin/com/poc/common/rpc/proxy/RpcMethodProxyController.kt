package com.poc.common.rpc.proxy

import com.poc.common.rpc.exception.RpcException
import com.poc.common.rpc.model.RpcRequestPayload
import com.poc.common.rpc.model.RpcResponsePayload
import jakarta.servlet.http.HttpServletRequest
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.memberFunctions

class RpcMethodProxyController(
    private val bean: Any
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @ResponseBody
    fun proxy(
        request: HttpServletRequest,
        @RequestBody payload: RpcRequestPayload
    ): RpcResponsePayload {
        val rpcId = request.servletPath.split("/").last()
        val rpcResponse = RpcResponsePayload()

        // try-catch
        try {
            // find function
            logger.info("00000 $payload ")
            val kfunction = bean::class.memberFunctions.firstOrNull { kfun ->
                (kfun.name == payload.methodName) and
                (kfun.parameters.size == payload.parameters.size + 1)
            } ?: throw RpcException("ID=$rpcId method=${payload.methodName} paramCount=${payload.parameters.size} is not found")

            // invoke call
            rpcResponse.data = if (kfunction.isSuspend) {
                runBlocking { kfunction.callSuspend(bean, *payload.parameters.toTypedArray()) }
            } else {
                kfunction.call(bean, *payload.parameters.toTypedArray())
            }

        // propagate error as message to the caller
        } catch (exception: Exception) {
            val message = if (exception is RpcException) {
                exception.message
            } else {
                "error on invoking bean=${bean.javaClass.simpleName}. " +
                "method=${payload.methodName}. " +
                "paramsCount=${payload.parameters.size}. " +
                "message=${exception.message ?: exception.stackTraceToString()}"
            }
            logger.error("[RpcMethodProxyController] $message", exception)
            rpcResponse.success = false
            rpcResponse.message = message
        }

        return rpcResponse
    }
}