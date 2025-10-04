package com.poc.common.rpc.model

/**
 * Represents the payload for an RPC (Remote Procedure Call) request.
 *
 * This class is used to encapsulate the necessary information required
 * to invoke a remote method call, including the method name and its parameters.
 *
 * Primary Responsibilities:
 * - Define the method name of the RPC call.
 * - Provide the list of parameters to be passed to the remote method.
 *
 * Key Characteristics:
 * - `methodName` specifies the name of the target method on the RPC service.
 * - `parameters` holds the arguments to be supplied to the remote method.
 * - Both properties can have default values for flexibility during initialization.
 */
data class RpcRequestPayload(
    val methodName: String = "",
    val parameters: List<Any?> = emptyList(),
)
