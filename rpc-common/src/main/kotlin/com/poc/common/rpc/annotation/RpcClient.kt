package com.poc.common.rpc.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class RpcClient(
    val id: String
)
