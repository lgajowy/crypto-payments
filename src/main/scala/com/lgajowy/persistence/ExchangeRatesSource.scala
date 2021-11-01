package com.lgajowy.persistence

import com.lgajowy.domain.{CoinCurrency, ExchangeRate, FiatCurrency}

/**
  * Note: Exchange repository assumes that the exchange rate for a given
  *
  */
class ExchangeRatesSource {
  def getEurExchangeRate(fiatCurrency: FiatCurrency): Option[ExchangeRate] = {
    MarketData.exchangeRatesToEUR.get(fiatCurrency.value).map(ExchangeRate)
  }

  def getCoinExchangeRate(fiatCurrency: FiatCurrency, coinCurrency: CoinCurrency): Option[ExchangeRate] = {
    coinCurrency match {
      case CoinCurrency("BTC") =>
        MarketData.exchangeRatesOfBTC
          .get(fiatCurrency.value)
          .map(value => BigDecimal(1) / value)
          .map(ExchangeRate)
      case _ => None
    }
  }

  private object MarketData {
    val exchangeRatesOfBTC: Map[String, BigDecimal] =
      Map("EUR" -> 42000.0, "USD" -> 50000.0)
    val exchangeRatesToEUR: Map[String, BigDecimal] =
      Map("EUR" -> 1.0, "USD" -> 0.84, "BTC" -> 42000.0)
  }
}

object ExchangeRatesSource {
  def apply(): ExchangeRatesSource = new ExchangeRatesSource()
}
