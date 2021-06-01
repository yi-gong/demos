
# demos

## Basic
A high avalibility and resiliant distributed system. The application is designed to deploy in a K8S cluster system.

## REST Endpoint
- Endpoint /currency/ for all supported currencies
- Endpoint /currency/exchangeRate for get exchange rate for source and target currency, e.g. /currency/exchangeRate?source=EUR&target=USD
- Endpoint /currency/change for change money from source currency to target currency, e.g. /currency/change?source=EUR&target=USD&money=20

## Modules:
### currency-rest-service:
- Public REST endpoint
- Internal GRPC endpoint to receive push of changed exchange rate.

### exchange-rate-service:
- Internal GRPC endpoint to receive request for exchange rate.


## Architecture design:

- currency-rest-service holds a local cache of base exchange rate. This application can scale as much as possible.  It garantees any request can be handled with a very low latency.
- exchange-rate-service designed as a single instance service, which request for external quelle in a daily based interval.  And broadcasing the changed data to all instances of currency-rest-service.
