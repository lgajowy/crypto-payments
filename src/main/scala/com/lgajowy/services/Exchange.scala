package com.lgajowy.services

import com.lgajowy.domain._
import com.lgajowy.persistence.MarketData

class Exchange {

  def exchangeToEUR(fiatAmount: FiatAmount, fiatCurrency: FiatCurrency): FiatAmount = {
    val rate: BigDecimal = MarketData.exchangeRatesToEUR(fiatCurrency.value)
    FiatAmount(fiatAmount.value * rate)
  }

  // TODO fix to use not only BTC
  def exchangeToCoin(amount: FiatAmount, currency: FiatCurrency, coinCurrency: CoinCurrency): CoinAmount = {
    val rate: BigDecimal = MarketData.exchangeRatesOfBTC(currency.value)
    CoinAmount(amount.value * rate)
  }

  def getExchangeRate(fiatCurrency: FiatCurrency, coinCurrency: CoinCurrency): ExchangeRate = {
    ExchangeRate(MarketData.exchangeRatesOfBTC(fiatCurrency.value))
  }

  def getEurExchangeRate(fiatCurrency: FiatCurrency): EurExchangeRate = {
    EurExchangeRate(MarketData.exchangeRatesToEUR(fiatCurrency.value))
  }
}

object Exchange {
  def apply() = new Exchange()
}
