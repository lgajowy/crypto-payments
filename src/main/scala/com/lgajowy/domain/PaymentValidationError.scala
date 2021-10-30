package com.lgajowy.domain

sealed trait PaymentValidationError

case class OutOfEURPriceRange(amount: BigDecimal) extends PaymentValidationError
case class UnsupportedFiatCurrency(currency: String) extends PaymentValidationError
case class UnsupportedCryptoCurrency(currency: String) extends PaymentValidationError
