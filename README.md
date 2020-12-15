# crypto-orderbook

## Setup
- Based on Spring Boot Application
- Java 14
- Dependencies
  - Lombok
  - Reactor

## Run
```
java -jar target/bw-0.1.jar
```

## Test
```
mvn surefire:test 
```
  
## Endpoints
 - Start the process to update the price
 ```
  curl -X GET http://localhost:8080/start
  ```
  - Create Account 
 ```
  curl -X POST http://localhost:8080/account \
  -H 'content-type: application/json' \
  -d '{
	"name": "benj",
	"usd_balance" : "2000.0",
  }'
  ```
  Return the {account Id} in the response
  
 - Fetch Account
 ```
  curl -X GET http://localhost:8080/accounts/{account Id}
  ```
  
 - Create Limit Order 
 ```
  curl -X POST http://localhost:8080/order \
  -H 'content-type: application/json' \
  -d '{
	"account_id": "1044841625",
	"price_limit" : "3200.0",
	"amount" : "3.0"
  }'
  ```
  Return the {order Id} in the response
  
 - Fetch Limit Order
 ```
  curl -X GET http://localhost:8080/orders/{order Id}

  ```
  
  ## Results
  Execute orders on price update.

  
  
