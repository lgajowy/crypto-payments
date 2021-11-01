package com.lgajowy.persistence

import com.lgajowy.domain._

trait Database {
  def insertPayment(payment: Payment): Unit

  def selectPaymentsByFiatCurrency(currency: FiatCurrency): List[Payment]

  def selectPaymentById(id: PaymentId): Option[Payment]

  def countAllPayments(): TotalPaymentsCount

  def countPaymentsForFiatCurrency(fiatCurrency: FiatCurrency): FiatCurrencyPaymentsCount

  def selectSupportedCryptoCurrencies(): List[CoinCurrency]

  def selectSupportedFiatCurrencies(): List[FiatCurrency]
}

object Database {
  def make(): Database = new Database {
    private val fiatCurrencies: List[String] = List("EUR", "USD")
    private val cryptoCurrencies: List[String] = List("BTC")
    private var payments: List[Payment] = List.empty

    override def insertPayment(payment: Payment): Unit = {
      payments = payments :+ payment
    }

    override def selectPaymentsByFiatCurrency(currency: FiatCurrency): List[Payment] =
      payments.filter(_.fiatCurrency == currency)

    override def selectPaymentById(id: PaymentId): Option[Payment] = payments.find(_.id == id)

    override def countAllPayments(): TotalPaymentsCount = TotalPaymentsCount(payments.size)

    override def countPaymentsForFiatCurrency(fiatCurrency: FiatCurrency): FiatCurrencyPaymentsCount =
      FiatCurrencyPaymentsCount(payments.count(_.fiatCurrency == fiatCurrency))

    override def selectSupportedCryptoCurrencies(): List[CoinCurrency] = cryptoCurrencies.map(CoinCurrency)

    override def selectSupportedFiatCurrencies(): List[FiatCurrency] = fiatCurrencies.map(FiatCurrency)
  }
}
