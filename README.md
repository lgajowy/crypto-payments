# Crypto payments app

A simple demo application emulating crpyto payments registry. Written using:

- akka http
- spray json library
- tapir
- cats (`Validated` type)
- pureconfig

## Running instructions

`sbt run`

## API

See the postman collection: in `payments-postman.json` export file.

## Encountered problems:

1. The task requirements suggest using Validated (Applicative Functor) type instead of Either (a Monad). However, during
   solving the task, I think I've found out that one of the validation steps depends on a prerequisite:

> "check if fiatAmount fits the EUR (min/max) price range defined in the application.conf" (needs support for fiat currency conversion)


vs

> "check if requested currency pair is supported by the API" (the requested fiat currency can be not supported)

I implemented a pre-validation check to see if `MarketData` contains the fiat currency EUR exchange rate. It made the
code more complicated which I'm not happy about but this was required to avoid the problem. I would consider using
Either Monad and validate sequentially (instead of independently thanks to an Applicative Functor) in this case and
probably drop `Validated` completely as there is a dependency between the two validations.

## What would I add if I had more time:

1. Simplify the MarketData exchange rates maps. For storing the Exchange Rates I would consider a structure like below
   (map):

```
(fromCurrency, toCurrency) => exchangeRate

eg: 

("EUR", "PLN") => 4.00,
("PLN", "EUR") => 0.25,
("BTC", "PLN") => ... 

``` 

The advantage is that whenever a new currency needs to be added, we just add the map entries. Current design requires
creating new fields in MarketData per new cryptocurrencies and adding new logic.

2. More cryptocurrency support. Currently, the app supports only BTC.

3. More tests in general. I didn't test the stats endpoint much, nor did I test every corner case. I would definitely do
   that in a real application. Here I just wanted to demonstrate some basic testing abilities due to limited time.

4. Swagger documentation instead of Postman. Thanks to tapir it's easy to add because we already have the description of
   the endpoints in endpoints.scala file. Tapir has an interpreter to OpenAPI files that could be utilized.

5. Property based tests. That would require providing generators for the custom types. Could prove useful in testing the
   Exchange (I'd expect that the generative tests would find more edge cases there than me).
   
