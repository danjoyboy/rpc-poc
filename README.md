# RPC POC

## Architecture
```
Air Service -> Ankle Service
```

## Declare RPC Servlet on Server (Ankle-Service)
https://github.com/danjoyboy/rpc-poc/blob/main/air-impl/src/main/kotlin/com/poc/air/service/CalculatorServiceImpl.kt
```
@Service
@RpcServlet("calculator")
class CalculatorServiceImpl: CalculatorService { ... }
```

## Define Config on Client (Air-Service)
https://github.com/danjoyboy/rpc-poc/blob/main/ankle-springboot/src/main/resources/application-dev.yml
```
rpc:
  calculator:
    host: http://localhost:8080
    timeoutMs: 3000
```

## Declare RPC Client Config on Client (Air-Service)
https://github.com/danjoyboy/rpc-poc/blob/main/ankle-springboot/src/main/kotlin/com/poc/ankle/configuration/RpcClientConfiguration.kt
```
@Configuration
class RpcClientConfiguration(
    private val rpcClientProxyFactory: RpcClientProxyFactory
) {

    @Bean
    fun calculator(): CalculatorService {
        return rpcClientProxyFactory.createProxy("calculator")
    }
}
```
This `rpcClientProxyFactory` is auto-configured from the rpc-common library

## Call Procedure from air service
```bash
curl --location 'localhost:8080/rpc/calculator' \
--header 'Content-Type: application/json' \
--data '{
    "methodName": "plus",
    "parameters": [1, 123123]
}'
```

## Call Procedure from ankle service
```bash
curl --location 'localhost:9000/rpc/math' \
--header 'Content-Type: application/json' \
--data '{
    "methodName": "doPlus",
    "parameters": []
}'
```