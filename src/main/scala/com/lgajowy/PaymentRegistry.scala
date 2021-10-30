package com.lgajowy

import cats.data.ValidatedNec
import cats.implicits.{ catsSyntaxValidatedIdBinCompat0, _ }
import com.lgajowy.domain._
import com.lgajowy.persistence.DB

import java.util.UUID

class PaymentRegistry(config: PaymentConfig) {
  private type ValidationResult[A] = ValidatedNec[PaymentError, A]

  def createPayment(paymentToCreate: PaymentToCreate): Either[List[PaymentError], Unit] = {
    (
      validatePriceRange(paymentToCreate.fiatAmount),
      validateFiatCurrencySupport(paymentToCreate.fiatCurrency),
      validateCryptoCurrencySupport(paymentToCreate.coinCurrency)
    ).mapN(ValidatedPaymentToCreate)
      .map(
        request =>
          Payment(
            UUID.randomUUID(),
            request.fiatAmount,
            request.fiatCurrency,
            convert(request.fiatAmount, request.fiatCurrency, request.coinCurrency),
            request.coinCurrency
          )
      )
      .map(DB.insertPayment)
      .toEither
      .left
      .map(_.toChain.toList)
  }

  // TODO: Implement
  def convert(fiatAmount: BigDecimal, fiatCurrency: String, coinCurrency: String): BigDecimal = fiatAmount

  private def validatePriceRange(fiatAmount: BigDecimal): ValidationResult[BigDecimal] =
    if (fiatAmount > config.minEurAmount && fiatAmount < config.maxEurAmount) {
      fiatAmount.validNec
    } else {
      OutOfEURPriceRange(fiatAmount).invalidNec
    }

  private def validateCryptoCurrencySupport(coinCurrency: String): ValidationResult[String] =
    if (DB.selectSupportedCryptoCurrencies().contains(coinCurrency)) {
      coinCurrency.validNec
    } else {
      UnsupportedCryptoCurrency(coinCurrency).invalidNec
    }

  private def validateFiatCurrencySupport(fiatCurrency: String): ValidationResult[String] =
    if (DB.selectSupportedFiatCurrencies().contains(fiatCurrency)) {
      fiatCurrency.validNec
    } else {
      UnsupportedFiatCurrency(fiatCurrency).invalidNec
    }

  def getPayments(currency: String): List[Payment] = {
    DB.selectPaymentsByFiatCurrency(currency)
  }
}

object PaymentRegistry {
  def apply(config: PaymentConfig) = new PaymentRegistry(config)
}
