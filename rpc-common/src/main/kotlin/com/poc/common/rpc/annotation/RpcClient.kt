package com.poc.common.rpc.annotation

/**
 * Annotation used to mark a field as an RPC (Remote Procedure Call) client.
 * Fields annotated with `@RpcClient` are automatically identified and processed,
 * allowing for the dynamic injection of RPC client implementations at runtime.
 *
 * This annotation is primarily used in distributed systems or microservice architectures
 * where communication between services is established through RPC mechanisms.
 *
 * The `id` property specifies the unique identifier of the RPC client. This identifier is used
 * to match the field with the corresponding RPC client implementation configured in the system.
 *
 * @property id The unique identifier for the RPC client. This value is mandatory and is used
 *              to resolve the correct client implementation for injection.
 * @see com.poc.common.rpc.annotation.RpcServlet
 * @see com.poc.common.rpc.annotation.RpcClientBean
 * @see com.poc.common.rpc.processor.RpcClientBeanPostProcessor
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class RpcClient(
    val id: String
)
