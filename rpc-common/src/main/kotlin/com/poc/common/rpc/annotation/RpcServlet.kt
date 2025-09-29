package com.poc.common.rpc.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class RpcServlet(
    val id: String
)