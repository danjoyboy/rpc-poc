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

class RpcClientBeanPostProcessor: BeanPostProcessor, ApplicationContextAware {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val rpcClientBeanMap = HashMap<String, Any>()
    private lateinit var applicationContext: ApplicationContext

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

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