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

class RpcHostBeanPostProcessor(
    private val requestMappingHandlerMapping: RequestMappingHandlerMapping
) : BeanPostProcessor {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Throws(BeansException::class)
    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        return bean
    }

    @Throws(BeansException::class)
    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        // Check if the bean is annotated with `@RpcServlet`
        if (bean.javaClass.isAnnotationPresent(RpcServlet::class.java)) {
            val rpcId = bean.javaClass
                .getAnnotation(RpcServlet::class.java)
                .id
            registerClassAsEndpoint(rpcId, bean)
        }
        return bean
    }

    // Dynamically register each class as an HTTP endpoint
    private fun registerClassAsEndpoint(rpcId: String, bean: Any) {
        val path = "/rpc/$rpcId"
        val controller = RpcMethodProxyController(bean)
        val method = controller.javaClass.getMethod("proxy", HttpServletRequest::class.java, RpcRequestPayload::class.java)
        val handlerMethod = HandlerMethod(controller, method)

        // Register the new endpoint
        val mappingInfo = RequestMappingInfo
            .paths(path)
            .methods(RequestMethod.POST)
            .build()
        requestMappingHandlerMapping.registerMapping(mappingInfo, controller, handlerMethod.method)

        logger.info("[RpcServletBeanPostProcessor] `$rpcId` RPC is registered")
    }
}