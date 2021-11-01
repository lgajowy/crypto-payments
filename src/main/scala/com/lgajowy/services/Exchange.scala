package com.lgajowy.services

import com.lgajowy.domain._
import com.lgajowy.persistence.MarketDataSource

class Exchange(marketData: MarketDataSource) {

  def exchangeToCoin(amount: FiatAmount, exchangeRate: ExchangeRate): CoinAmount =
    CoinAmount(amount.value * exchangeRate.value)

  def exchangeToFiat(amount: FiatAmount, exchangeRate: ExchangeRate): FiatAmount =
    FiatAmount(amount.value * exchangeRate.value)

  def getEurExchangeRate(fiatCurrency: FiatCurrency): Option[EurExchangeRate] = {
    marketData.exchangeRatesToEUR.get(fiatCurrency.value).map(ExchangeRate).map(EurExchangeRate)
  }

  def getCoinExchangeRate(fiatCurrency: FiatCurrency, coinCurrency: CoinCurrency): Option[ExchangeRate] = {
    coinCurrency match {
      case CoinCurrency("BTC") =>
        marketData.exchangeRatesOfBTC
          .get(fiatCurrency.value)
          .map(value => BigDecimal(1) / value)
          .map(ExchangeRate)
      case _ => None
    }
  }
}

object Exchange {
  def apply(ratesSource: MarketDataSource) = new Exchange(ratesSource)
}
