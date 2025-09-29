# RPC POC

## Architecture
```
Air Service -> Ankle Service
```

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