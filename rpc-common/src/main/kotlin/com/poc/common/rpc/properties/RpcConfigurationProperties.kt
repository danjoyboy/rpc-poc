package com.poc.common.rpc.properties

import com.poc.common.rpc.model.RpcConfig
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration properties for defining RPC (Remote Procedure Call) configurations in the application.
 * This class extends a `HashMap` where the key represents the RPC service identifier, and the value is
 * an instance of `RpcConfig` containing the configuration for that service.
 *
 * These properties are typically loaded from the application's configuration file (e.g., `application.yml`
 * or `application.properties`) under the `rpc` namespace. Each RPC service can define its host and other
 * properties using this structure.
 *
 * Primary Usage:
 * - Provides a centralized way to manage multiple RPC service configurations.
 * - Allows dynamic creation of RPC client proxies based on the loaded configurations.
 *
 * Example Structure in Configuration File:
 *
 * ```
 * rpc:
 *   serviceA:
 *     host: "http://service-a.example.com"
 *     timeoutMs: 5000
 *   serviceB:
 *     host: "http://service-b.example.com"
 *     timeoutMs: 10000
 * ```
 *
 * Key Characteristics:
 * - Each configuration entry corresponds to an `RpcConfig` object.
 * - Integrates seamlessly with Spring's `@EnableConfigurationProperties` to bind the configuration at runtime.
 * - Can be injected into other components for accessing RPC-specific configurations programmatically.
 */
@ConfigurationProperties("rpc")
class RpcConfigurationProperties : HashMap<String, RpcConfig>()