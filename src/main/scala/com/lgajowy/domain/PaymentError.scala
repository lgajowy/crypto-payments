package com.lgajowy.domain

sealed trait PaymentError {
  def message: String
}

sealed trait PaymentRequestValidationError extends PaymentError

case class OutOfEURPriceRange(amount: FiatAmount, currency: FiatCurrency) extends PaymentRequestValidationError {
  def message = s"The price is out of EUR range. Price: ${amount.value} ${currency.value}"
}
case class UnsupportedFiatCurrency(currency: FiatCurrency) extends PaymentRequestValidationError {
  def message = s"Unsupported fiat currency: ${currency.value}"
}

case class UnknownFiatCurrencyExchangeRate(currency: FiatCurrency) extends PaymentRequestValidationError {
  def message = s"Unknown fiat currency exchange rate: ${currency.value}"
}

case class UnsupportedCryptoCurrency(currency: CoinCurrency) extends PaymentRequestValidationError {
  def message = s"Unsupported crypto currency: ${currency.value}"
}

case class PaymentNotFound(id: PaymentId) extends PaymentError {
  def message = s"Couldn't find payment with id: ${id.value}"
}
