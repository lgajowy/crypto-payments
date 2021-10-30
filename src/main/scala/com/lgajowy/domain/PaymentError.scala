package com.lgajowy.domain

sealed trait PaymentError {
  def message: String
}

case class OutOfEURPriceRange(amount: BigDecimal) extends PaymentError {
  def message = s"The price is out of range. Price: $amount"
}
case class UnsupportedFiatCurrency(currency: String) extends PaymentError {
  def message = s"Unsupported fiat currency: $currency"
}
case class UnsupportedCryptoCurrency(currency: String) extends PaymentError {
  def message = s"Unsupported crypto currency: $currency"
}
