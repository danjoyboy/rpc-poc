package com.poc.common.rpc.properties

import com.poc.common.rpc.model.RpcConfig
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("rpc")
class RpcConfigurationProperties : HashMap<String, RpcConfig>()