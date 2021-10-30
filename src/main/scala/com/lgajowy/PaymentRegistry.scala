package com.lgajowy

import cats.data.ValidatedNec
import cats.implicits.{catsSyntaxValidatedIdBinCompat0, _}
import com.lgajowy.configuration.PaymentConfig
import com.lgajowy.domain._
import com.lgajowy.persistence.DB

import java.util.UUID

class PaymentRegistry(config: PaymentConfig) {

  private type ValidationResult[A] = ValidatedNec[PaymentRequestValidationError, A]

  def createPayment(paymentToCreate: PaymentToCreate): Either[List[PaymentRequestValidationError], Unit] = {
    (
      validatePriceRange(paymentToCreate.fiatAmount),
      validateFiatCurrencySupport(paymentToCreate.fiatCurrency),
      validateCryptoCurrencySupport(paymentToCreate.coinCurrency)
    ).mapN(ValidatedPaymentToCreate)
      .map(
        request =>
          Payment(
            PaymentId(UUID.randomUUID()),
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
  def convert(fiatAmount: FiatAmount, fiatCurrency: FiatCurrency, coinCurrency: CoinCurrency): CoinAmount =
    CoinAmount(fiatAmount.value)

  private def validatePriceRange(fiatAmount: FiatAmount): ValidationResult[FiatAmount] =
    if (fiatAmount.value > config.minEurAmount && fiatAmount.value < config.maxEurAmount) {
      fiatAmount.validNec
    } else {
      OutOfEURPriceRange(fiatAmount).invalidNec
    }

  private def validateCryptoCurrencySupport(coinCurrency: CoinCurrency): ValidationResult[CoinCurrency] =
    if (DB.selectSupportedCryptoCurrencies().contains(coinCurrency)) {
      coinCurrency.validNec
    } else {
      UnsupportedCryptoCurrency(coinCurrency).invalidNec
    }

  private def validateFiatCurrencySupport(fiatCurrency: FiatCurrency): ValidationResult[FiatCurrency] =
    if (DB.selectSupportedFiatCurrencies().contains(fiatCurrency)) {
      fiatCurrency.validNec
    } else {
      UnsupportedFiatCurrency(fiatCurrency).invalidNec
    }

  def getPayments(currency: FiatCurrency): List[Payment] = {
    DB.selectPaymentsByFiatCurrency(currency)
  }

  def findPayment(id: PaymentId): Either[PaymentNotFound, Payment] = {
    DB.selectPaymentById(id) match {
      case Some(value) => Right(value)
      case None        => Left(PaymentNotFound(id))
    }
  }

  // TODO: Future + Applicative?
  def getPaymentStats(fiatCurrency: FiatCurrency): PaymentStats =
    PaymentStats(
      DB.countAllPayments(),
      DB.countPaymentsForFiatCurrency(fiatCurrency)
    )
}

object PaymentRegistry {
  def apply(config: PaymentConfig) = new PaymentRegistry(config)
}
