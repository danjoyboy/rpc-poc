package com.poc.common.rpc.model

data class RpcResponsePayload(
    var data: Any? = null,
    var success: Boolean = true,
    var message: String? = null
)
