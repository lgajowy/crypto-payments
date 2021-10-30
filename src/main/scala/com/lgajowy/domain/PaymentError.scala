package com.lgajowy.domain

sealed trait PaymentError {
  def message: String
}

case class OutOfEURPriceRange(amount: FiatAmount) extends PaymentError {
  def message = s"The price is out of range. Price: ${amount.value}"
}
case class UnsupportedFiatCurrency(currency: FiatCurrency) extends PaymentError {
  def message = s"Unsupported fiat currency: ${currency.value}"
}
case class UnsupportedCryptoCurrency(currency: CoinCurrency) extends PaymentError {
  def message = s"Unsupported crypto currency: ${currency.value}"
}
