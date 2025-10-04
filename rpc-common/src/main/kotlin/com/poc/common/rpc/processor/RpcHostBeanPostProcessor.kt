package com.poc.common.rpc.processor

import com.poc.common.rpc.annotation.RpcServlet
import com.poc.common.rpc.model.RpcRequestPayload
import com.poc.common.rpc.proxy.RpcMethodProxyController
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

/**
 * A specialized implementation of the `BeanPostProcessor` interface that processes Spring beans
 * annotated with `@RpcServlet`. This class dynamically registers such beans as RPC (Remote Procedure Call)
 * endpoints in the application's HTTP request mapping system.
 *
 * The processor interacts with the `RequestMappingHandlerMapping` to register endpoint paths and their
 * associated handler methods. The primary purpose is to enable automated registration of RPC services
 * in a Spring context.
 *
 * @constructor Creates an `RpcHostBeanPostProcessor` with the provided `RequestMappingHandlerMapping`
 * to manage endpoint registration.
 *
 * @param requestMappingHandlerMapping A Spring component responsible for managing HTTP request mappings,
 *                                      required for dynamically registering RPC endpoints.
 *
 * Implements:
 * - `postProcessBeforeInitialization`: No special operations are performed in this method.
 * - `postProcessAfterInitialization`: Detects `@RpcServlet` annotations on beans and registers them
 *   as endpoints using the `registerClassAsEndpoint` method.
 *
 * Key Functionality:
 * - Identifies beans annotated with `@RpcServlet`.
 * - Dynamically maps the annotated bean as an HTTP POST endpoint with a specified or default path.
 * - Provides logging for successful endpoint registrations.
 */
class RpcHostBeanPostProcessor(
    private val requestMappingHandlerMapping: RequestMappingHandlerMapping
) : BeanPostProcessor {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Throws(BeansException::class)
    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        return bean
    }

    /**
     * Processes the given bean instance after its initialization, allowing for custom modifications.
     * Specifically checks if the bean is annotated with `@RpcServlet` and registers it as an RPC endpoint.
     *
     * @param bean The bean instance being processed by the BeanPostProcessor.
     * @param beanName The name of the bean in the application context.
     * @return The possibly modified (or the original, unmodified) bean instance.
     * @throws BeansException If any error occurs during the processing.
     */
    @Throws(BeansException::class)
    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        if (bean.javaClass.isAnnotationPresent(RpcServlet::class.java)) {
            val annot = bean.javaClass.getAnnotation(RpcServlet::class.java)
            registerClassAsEndpoint(annot, bean)
        }
        return bean
    }

    /**
     * Registers a class annotated with `@RpcServlet` as an RPC endpoint.
     * This method dynamically maps the annotated class to a specified HTTP endpoint
     * using the given annotation properties.
     *
     * @param annot The `RpcServlet` annotation instance containing metadata about the endpoint,
     *              such as its unique ID and optional custom path.
     * @param bean  The class instance annotated with `RpcServlet` to be registered as the endpoint.
     */
    private fun registerClassAsEndpoint(
        annot: RpcServlet,
        bean: Any
    ) {
        // route controller
        val controller = RpcMethodProxyController(annot.id, bean)
        val method = controller.javaClass.getMethod(
            "proxy",
            HttpServletRequest::class.java,
            RpcRequestPayload::class.java
        )
        val handlerMethod = HandlerMethod(controller, method)

        // Register the new endpoint
        val servletPath = annot.path.takeIf { it.isNotBlank() } ?: "/rpc/${annot.id}"
        val mappingInfo = RequestMappingInfo
            .paths(servletPath)
            .methods(RequestMethod.POST)
            .build()
        requestMappingHandlerMapping.registerMapping(mappingInfo, controller, handlerMethod.method)

        logger.info("[RpcServletBeanPostProcessor] RPC `${annot.id}` is registered under $servletPath")
    }
}