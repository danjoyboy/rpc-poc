package com.poc.common.rpc.annotation

/**
 * Annotation used for marking a class as an RPC (Remote Procedure Call) servlet.
 * Classes annotated with `@RpcServlet` are dynamically registered as RPC endpoints
 * that can handle specific RPC requests within the application.
 *
 * The annotation specifies metadata required during registration of the servlet,
 * including a unique identifier (`id`) and an optional custom path (`path`) for the endpoint.
 *
 * If `path` is not provided, a default path is automatically generated using the `id`.
 *
 * Primary usage includes binding annotated implementations as executable RPC endpoints
 * in a distributed system or microservice architecture.
 *
 * @property id The unique identifier of the RPC servlet. This is mandatory and used to distinguish the endpoint.
 * @property path The optional custom path for the RPC servlet. If left empty, a default path will be generated.
 * @see com.poc.common.rpc.processor.RpcHostBeanPostProcessor
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class RpcServlet(
    val id: String,
    val path: String = ""
)
