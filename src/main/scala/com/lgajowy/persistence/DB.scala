package com.lgajowy.persistence

import com.lgajowy.domain.Payment

import java.util.UUID

object DB {

  private val fiatCurrencies: List[String] = List("EUR", "USD")
  private val cryptoCurrencies: List[String] = List("BTC")
  private var payments: List[Payment] = List.empty

  def insertPayment(payment: Payment): Unit = {
    payments = payments :+ payment
  }

  def selectPaymentsByFiatCurrency(currency: String): List[Payment] =
    payments.filter(_.fiatCurrency == currency)

  def selectPaymentById(id: UUID): Option[Payment] = payments.find(_.id == id)

  def countPaymentsByFiatCurrency(currency: String): Int = payments.count(_.fiatCurrency == currency)
}
