package com.lgajowy.services

import com.lgajowy.domain._
import com.lgajowy.persistence.ExchangeRatesSource

class Exchange(ratesSource: ExchangeRatesSource) {

  def exchangeToCoin(amount: FiatAmount, exchangeRate: ExchangeRate) = CoinAmount(amount.value * exchangeRate.value)

  def exchangeToFiat(amount: FiatAmount, exchangeRate: ExchangeRate) = FiatAmount(amount.value * exchangeRate.value)

  def getEurExchangeRate(currency: FiatCurrency): Option[EurExchangeRate] =
    ratesSource.getEurExchangeRate(currency).map(EurExchangeRate)

  def getCoinExchangeRate(fiatCurrency: FiatCurrency, coinCurrency: CoinCurrency): Option[ExchangeRate] =
    ratesSource.getCoinExchangeRate(fiatCurrency, coinCurrency)
}

object Exchange {
  def apply(ratesSource: ExchangeRatesSource) = new Exchange(ratesSource)
}
