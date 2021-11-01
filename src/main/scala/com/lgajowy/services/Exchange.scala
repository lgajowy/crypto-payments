package com.lgajowy.services

import com.lgajowy.domain._
import com.lgajowy.persistence.ExchangeRatesRepository

class Exchange(ratesRepository: ExchangeRatesRepository) {

  def exchangeToCoin(amount: FiatAmount, exchangeRate: ExchangeRate) = CoinAmount(amount.value * exchangeRate.value)

  def getEurExchangeRate(currency: FiatCurrency): Option[EurExchangeRate] =
    ratesRepository.getEurExchangeRate(currency).map(EurExchangeRate)

  def getCoinExchangeRate(fiatCurrency: FiatCurrency, coinCurrency: CoinCurrency): Option[ExchangeRate] =
    ratesRepository.getCoinExchangeRate(fiatCurrency, coinCurrency)

  def exchangeToEur(amount: FiatAmount, currency: FiatCurrency): Either[UnknownFiatCurrencyExchangeRate, FiatAmount] = {
    val exchangeRate: Option[ExchangeRate] = ratesRepository.getEurExchangeRate(currency)

    exchangeRate match {
      case None       => Left(UnknownFiatCurrencyExchangeRate(currency))
      case Some(rate) => Right(exchangeToFiat(amount, rate))
    }
  }
  def exchangeToFiat(amount: FiatAmount, exchangeRate: ExchangeRate) =
    FiatAmount(amount.value * exchangeRate.value)
}

object Exchange {
  def apply(ratesRepository: ExchangeRatesRepository) = new Exchange(ratesRepository)
}
