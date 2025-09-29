package com.poc.common.rpc.model

data class RpcConfig (
    val host: String = "",
    val timeoutMs: Long = 0L,
)
