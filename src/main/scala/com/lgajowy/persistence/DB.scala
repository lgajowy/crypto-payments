package com.lgajowy.persistence

import com.lgajowy.domain.{CoinCurrency, FiatCurrency, Payment, PaymentId}

import java.util.UUID

object DB {

  private val fiatCurrencies: List[String] = List("EUR", "USD")
  private val cryptoCurrencies: List[String] = List("BTC")
  private var payments: List[Payment] = List.empty

  def insertPayment(payment: Payment): Unit = {
    payments = payments :+ payment
  }

  def selectPaymentsByFiatCurrency(currency: FiatCurrency): List[Payment] =
    payments.filter(_.fiatCurrency == currency)

  def selectPaymentById(id: PaymentId): Option[Payment] = payments.find(_.id == id)

  def countPaymentsByFiatCurrency(currency: FiatCurrency): Int = payments.count(_.fiatCurrency == currency)

  def selectSupportedCryptoCurrencies(): List[CoinCurrency] = cryptoCurrencies.map(CoinCurrency)

  def selectSupportedFiatCurrencies(): List[FiatCurrency] = fiatCurrencies.map(FiatCurrency)
}
