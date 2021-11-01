package com.lgajowy.persistence

trait MarketDataSource {
  def exchangeRatesOfBTC: Map[String, BigDecimal]
  def exchangeRatesToEUR: Map[String, BigDecimal]
}

object MarketDataSource {
  def make(): MarketDataSource = new MarketDataSource {
    override val exchangeRatesOfBTC: Map[String, BigDecimal] =
      Map("EUR" -> 42000.0, "USD" -> 50000.0)
    override val exchangeRatesToEUR: Map[String, BigDecimal] =
      Map("EUR" -> 1.0, "USD" -> 0.84, "BTC" -> 42000.0)
  }
}
