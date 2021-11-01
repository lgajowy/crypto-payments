package com.lgajowy.configuration

import scala.concurrent.duration.FiniteDuration

case class Configuration(api: ApiConfig, routes: RoutesConfiguration)

case class ApiConfig(payment: PaymentConfig)

case class PaymentConfig(expiration: FiniteDuration, minEurAmount: BigDecimal, maxEurAmount: BigDecimal)

case class RoutesConfiguration(askTimeout: FiniteDuration)
