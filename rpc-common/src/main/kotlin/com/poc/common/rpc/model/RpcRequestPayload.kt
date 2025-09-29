package com.poc.common.rpc.model

data class RpcRequestPayload(
    val methodName: String = "",
    val parameters: List<Any> = emptyList(),
)
