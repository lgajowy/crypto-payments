package com.lgajowy.http.dto

import com.lgajowy.domain.{ CoinCurrency, FiatAmount, FiatCurrency, PaymentToCreate }

case class PaymentRequest(fiatAmount: BigDecimal, fiatCurrency: String, coinCurrency: String) {
  def toDomain(): PaymentToCreate = PaymentToCreate(
    FiatAmount(fiatAmount),
    FiatCurrency(fiatCurrency),
    CoinCurrency(coinCurrency)
  )
}
