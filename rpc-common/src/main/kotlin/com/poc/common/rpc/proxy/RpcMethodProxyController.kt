package com.poc.common.rpc.proxy

import com.poc.common.rpc.constant.RPC_HEADER
import com.poc.common.rpc.exception.RpcException
import com.poc.common.rpc.model.RpcRequestPayload
import com.poc.common.rpc.model.RpcResponsePayload
import jakarta.servlet.http.HttpServletRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.ResponseBody
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.memberFunctions

/**
 * RpcMethodProxyController is designed to handle the invocation of remote procedure calls (RPC)
 * on a given bean. It dynamically locates and invokes a method of the target bean based on the
 * RPC request payload provided. It supports both suspend and non-suspend Kotlin functions.
 *
 * @constructor
 * Creates an instance of RpcMethodProxyController with the provided target bean, which is
 * the object whose methods will be invoked during the processing of RPC requests.
 *
 * @param bean The target object containing the methods to be invoked via RPC.
 *
 * Primary Responsibilities:
 * - Parse the incoming HTTP request to determine the method to be invoked.
 * - Find the corresponding method in the provided bean using reflection.
 * - Handle method invocation, supporting both synchronous and asynchronous (suspend) functions.
 * - Return the result of the method execution encapsulated in an RpcResponsePayload.
 * - Log and gracefully handle any errors that occur during the invocation process, returning an
 *   appropriate error response to the client.
 *
 * Notes:
 * - The RpcRequestPayload encapsulates the method name and parameters for the invocation.
 * - The RpcResponsePayload contains the result or error message produced by the method invocation.
 * - If no matching method is found, or if the method invocation fails, an error response will be
 *   returned with detailed information for tracing the issue.
 *
 * Throws:
 * - RpcException if a matching method cannot be found or invocation fails with specific reasons
 *   related to the RPC workflow.
 */
class RpcMethodProxyController(
    private val id: String,
    private val bean: Any
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Handles an incoming RPC request by invoking a method dynamically on a managed bean
     * based on the payload information, and returns the response.
     *
     * @param request the HTTP servlet request containing context for the RPC invocation
     * @param payload the RPC request payload containing the method name and parameters to be invoked
     * @return the RPC response payload containing the result of the invoked method, success status, and message
     */
    @ResponseBody
    fun proxy(
        request: HttpServletRequest,
        @RequestBody payload: RpcRequestPayload
    ): RpcResponsePayload {
        val rpcResponse = RpcResponsePayload()

        try {
            // find function
            val kfunction = bean::class.memberFunctions.firstOrNull { kfun ->
                (kfun.name == payload.methodName) and
                (kfun.parameters.size == payload.parameters.size + 1)
            } ?: throw RpcException(
                "ID=$id method=${payload.methodName} " +
                "paramCount=${payload.parameters.size} is not found"
            )

            // invoke call
            rpcResponse.data = if (kfunction.isSuspend) {
                runBlocking(Dispatchers.IO) {
                    kfunction.callSuspend(bean, *payload.parameters.toTypedArray())
                }
            } else {
                kfunction.call(bean, *payload.parameters.toTypedArray())
            }

        // propagate the error as a message to the caller
        } catch (exception: Exception) {
            val message = if (exception is RpcException) {
                exception.message
            } else {
                "error on invoking bean=${bean.javaClass.simpleName}. " +
                "ID=$id. " +
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