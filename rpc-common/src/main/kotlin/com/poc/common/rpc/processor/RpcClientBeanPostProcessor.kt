package com.poc.common.rpc.processor

import com.poc.common.rpc.annotation.RpcClient
import com.poc.common.rpc.annotation.RpcClientBean
import com.poc.common.rpc.exception.RpcException
import org.slf4j.LoggerFactory
import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.util.ReflectionUtils

/**
 * A BeanPostProcessor implementation responsible for processing and injecting RPC (Remote Procedure Call)
 * client proxies into Spring-managed beans where fields are annotated with `@RpcClient`.
 *
 * The processor enables seamless integration of RPC client components by resolving and injecting
 * the required dependencies during the Spring bean lifecycle. It ensures that the correct RPC client
 * proxies are available for usage within the application context.
 *
 * Implements:
 * - `setApplicationContext`: Captures and stores the Spring ApplicationContext to facilitate dependency handling.
 * - `postProcessBeforeInitialization`: Processes all fields of a bean that are annotated with `@RpcClient`.
 *   Identifies the RPC client ID from the annotation, retrieves the corresponding client proxy from
 *   the internal registry, and injects it into the annotated field.
 * - `postProcessAfterInitialization`: Registers beans annotated with `@RpcClientBean` into an internal client registry.
 *   Ensures unique identification of each RPC client proxy.
 *
 * Key Features:
 * - Scans beans for `@RpcClient` annotations during initialization.
 * - Dynamically injects RPC client proxies into annotated fields.
 * - Maintains a registry of RPC client beans annotated with `@RpcClientBean`.
 * - Validates uniqueness of `@RpcClientBean` IDs to prevent conflicts.
 *
 * Logging:
 * - Logs successful injection of RPC clients.
 * - Logs errors for missing or duplicate RPC client IDs.
 *
 * Throws:
 * - RpcException: Raised if there is a missing or duplicated RPC client ID during processing.
 *
 * Dependencies:
 * - Uses `ReflectionUtils` for accessing and injecting private fields.
 * - Expects `@RpcClient` and `@RpcClientBean` annotations to be present in the context.
 *
 * Example Use Case:
 * This processor is useful in applications utilizing RPC calls between microservices, enabling
 * automatic configuration and injection of RPC clients by simply annotating fields with `@RpcClient`
 * and registering client beans with `@RpcClientBean`.
 */
class RpcClientBeanPostProcessor: BeanPostProcessor, ApplicationContextAware {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val rpcClientBeanMap = HashMap<String, Any>()
    private lateinit var applicationContext: ApplicationContext

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    /**
     * Processes the given bean instance before its initialization phase in the Spring lifecycle.
     * This method scans for fields annotated with `@RpcClient` and injects the corresponding
     * RPC client beans based on the annotation's `id`.
     *
     * If the `id` specified in the `@RpcClient` annotation does not exist within the `rpcClientBeanMap`,
     * an `RpcException` will be thrown.
     *
     * @param bean The bean instance currently being processed.
     * @param beanName The name of the bean in the application context.
     * @return The possibly modified (or the original, unmodified) bean instance.
     * @throws BeansException If an error occurs during processing.
     * @throws RpcException If the RPC client bean corresponding to the annotated field cannot be found.
     */
    @Throws(BeansException::class)
    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        bean.javaClass.declaredFields.forEach { field ->
            val rpcClientAnnotation = field.getAnnotation(RpcClient::class.java)
                ?: return@forEach
            val rpcId = rpcClientAnnotation.id
            val rpcClientBean = rpcClientBeanMap[rpcId] ?: throw RpcException("RpcClient of `$rpcId` is not exist")
            ReflectionUtils.makeAccessible(field)
            ReflectionUtils.setField(field, bean, rpcClientBean)
            logger.info("[RpcClientBeanPostProcessor] rpc client ID: $rpcId injected to ${bean.javaClass.simpleName}")
        }

        return bean
    }

    /**
     * Processes the given bean instance after its initialization in the Spring lifecycle.
     * This method detects and manages beans annotated with `@RpcClientBean`.
     * It ensures that RPC client beans are uniquely registered and added to the processing context.
     *
     * If a bean with the same `@RpcClientBean` ID is already present in the `rpcClientBeanMap`,
     * an `RpcException` will be thrown to prevent duplicate registrations.
     *
     * @param bean The bean instance being processed after initialization.
     * @param beanName The name of the bean in the application context.
     * @return The original or modified bean instance.
     * @throws BeansException If errors occur during the initialization process.
     * @throws RpcException If a duplicate RPC client ID is detected.
     */
    @Throws(BeansException::class)
    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        applicationContext
            .findAnnotationOnBean(beanName, RpcClientBean::class.java)
            ?.also {
                if (beanName in rpcClientBeanMap) {
                    throw RpcException("Duplicated RpcClient's ID for `$beanName`")
                }
                rpcClientBeanMap[beanName] = bean
            }

        return bean
    }
}