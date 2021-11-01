package com.lgajowy.services

import cats.data.{ NonEmptyChain, ValidatedNec }
import cats.implicits.{ catsSyntaxValidatedIdBinCompat0, _ }
import com.lgajowy.configuration.PaymentConfig
import com.lgajowy.domain._
import com.lgajowy.persistence.DB
import com.lgajowy.tools.UuidGenerator

import java.time.{ Clock, LocalDateTime }

class PaymentRegistry(config: PaymentConfig, exchange: Exchange)(implicit uuidGenerator: UuidGenerator, clock: Clock) {

  private type ValidationResult[A] = ValidatedNec[PaymentRequestValidationError, A]

  def createPayment(paymentToCreate: PaymentToCreate): Either[List[PaymentRequestValidationError], Unit] = {
    exchange.getEurExchangeRate(paymentToCreate.fiatCurrency) match {
      case None => Left(List(UnknownFiatCurrencyExchangeRate(paymentToCreate.fiatCurrency)))
      case Some(eurExchangeRate) =>
        val fiatInEur: FiatAmount = exchange.exchangeToFiat(paymentToCreate.fiatAmount, eurExchangeRate.rate)
        (
          validateFiatEURPriceRange(paymentToCreate.fiatAmount, paymentToCreate.fiatCurrency, fiatInEur),
          validateFiatCurrencySupport(paymentToCreate.fiatCurrency),
          validateCryptoCurrencySupport(paymentToCreate.coinCurrency)
        ).mapN(ValidatedPaymentToCreate)
          .map(request => createPayment(eurExchangeRate, request))
          .map(DB.insertPayment)
          .toEither
          .left
          .map(_.toChain.toList)
    }
  }

  private def createPayment(eurExchangeRate: EurExchangeRate, request: ValidatedPaymentToCreate) = {
    val now = LocalDateTime.now(clock)
    val expiresAt = now.plusNanos(config.expiration.toNanos)

    // FIXME: For now we just assume that the exchange rate is there since the validation has succeeded.
    val coinExchangeRate = exchange.getCoinExchangeRate(request.fiatCurrency, request.coinCurrency).get
    val coinAmount = exchange.exchangeToCoin(request.fiatAmount, coinExchangeRate)

    Payment(
      PaymentId(uuidGenerator.generate()),
      request.fiatAmount,
      request.fiatCurrency,
      coinAmount,
      request.coinCurrency,
      coinExchangeRate,
      eurExchangeRate,
      CreatedAt(now),
      ExpirationTime(expiresAt)
    )
  }

  private def validateFiatEURPriceRange(
    fiatAmount: FiatAmount,
    fiatCurrency: FiatCurrency,
    fiatInEur: FiatAmount
  ): ValidationResult[FiatAmount] = {
    if (fiatInEur.value > config.minEurAmount && fiatInEur.value < config.maxEurAmount) {
      fiatAmount.validNec
    } else {
      OutOfEURPriceRange(fiatAmount, fiatCurrency).invalidNec
    }
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
  def apply(config: PaymentConfig, exchange: Exchange)(implicit uuidGenerator: UuidGenerator, clock: Clock) =
    new PaymentRegistry(config, exchange)(uuidGenerator, clock)
}
