package com.poc.common.rpc.model

/**
 * Represents the configuration for an RPC (Remote Procedure Call) service.
 *
 * This class encapsulates settings required to connect to an RPC service, including the host URL
 * and the timeout duration for requests. It can be utilized in conjunction with configuration properties
 * to manage multiple service configurations dynamically or statically in an application.
 *
 * Primary Responsibilities:
 * - Define the target host URL for the RPC service.
 * - Specify the request timeout duration in milliseconds.
 *
 * Key Characteristics:
 * - The `host` field represents the base URL for making RPC requests.
 * - The `timeoutMs` field indicates the amount of time, in milliseconds, to wait before a request times out.
 */
data class RpcConfig (
    val host: String = "",
    val timeoutMs: Long = 0L,
)
