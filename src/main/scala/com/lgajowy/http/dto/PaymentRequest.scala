package com.lgajowy.http.dto

import com.lgajowy.domain.PaymentToCreate

case class PaymentRequest(fiatAmount: BigDecimal, fiatCurrency: String, coinCurrency: String) {
  def toDomain(): PaymentToCreate = PaymentToCreate(fiatAmount, fiatCurrency, coinCurrency)
}

