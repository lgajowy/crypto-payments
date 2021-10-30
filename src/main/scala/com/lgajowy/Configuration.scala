package com.lgajowy

import scala.concurrent.duration.FiniteDuration

case class Configuration(api: ApiConfig, routes: RoutesConfiguration)

case class ApiConfig(payment: PaymentConfig)

case class PaymentConfig(expiration: FiniteDuration, minEurAmount: Int, maxEurAmount: Int)

case class RoutesConfiguration(askTimeout: FiniteDuration)
