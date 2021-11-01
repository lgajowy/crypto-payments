# Crypto payments app

A simple demo application emulating crpyto payments registry. Written using:

- akka http
- spray json library
- tapir
- cats (Validated type)
- pureconfig

## Running instructions

`sbt run`

## API

See the postman collection: in `payments-postman.json` export file.

## Encountered problems:

1. The task requirements suggest using Validated (Applicative Functor) type instead of Either (a Monad). However, during
   solving the task, I think I've found out that one of the validation steps depends on a prerequisite:

   "check if fiatAmount fits the EUR (min/max) price range defined in the application.conf" (needs support for fiat
   currency conversion)
   vs
   "check if requested currency pair is supported by the API" (the requested fiat currency can be not supported)

   I implemented a pre-validation check to see if `MarketData` has the fiat currency EUR exchange rate. It made the code
   more complicated which I'm not happy about but this was required to avoid the problem. I would probably consider
   using Either Monad and validate sequentially (instead of independently thanks to an Applicative Functor) in this
   case. 

## What would I add if I had more time:

1. More tests in general. I didn't test the stats endpoint much, nor did I test every corner case. I would definitely do
   that in a real application. Here I just wanted to demonstrate some basic testing abilities.

2. Property based tests. That would require providing generators for the custom types. Could prove useful in testing the
   Exchange (I'd expect that the generative tests would find more edge cases there than me).

3. Swagger documentation. Thanks to tapir it's easy to add because we already have the description of the endpoints in
   endpoints.scala file. Tapir has an interpreter to OpenAPI files that could be utilized.
   
