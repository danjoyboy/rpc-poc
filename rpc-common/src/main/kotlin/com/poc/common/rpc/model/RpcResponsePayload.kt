package com.poc.common.rpc.model

/**
 * Represents the response payload for an RPC (Remote Procedure Call).
 *
 * This data class is used to encapsulate the result or status of an RPC execution. It includes
 * the data returned from the RPC method, a success indicator flag, and an optional message for
 * error handling or additional context.
 *
 * Primary Responsibilities:
 * - Contain the result data of the RPC execution.
 * - Indicate the success or failure status of the RPC.
 * - Provide an optional message for error reporting or additional details.
 *
 * Key Characteristics:
 * - `data` holds the result of the RPC method execution. It can be null if no data is returned.
 * - `success` indicates whether the RPC operation was executed successfully.
 * - `message` provides an optional message, typically used for error descriptions or status information.
 */
data class RpcResponsePayload(
    var data: Any? = null,
    var success: Boolean = true,
    var message: String? = null
)
