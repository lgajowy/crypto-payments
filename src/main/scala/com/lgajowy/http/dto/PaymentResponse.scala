package com.lgajowy.http.dto

import com.lgajowy.domain.Payment

import java.time.LocalDateTime
import java.util.UUID

final case class PaymentResponse(
  id: UUID,
  fiatAmount: BigDecimal,
  fiatCurrency: String,
  coinAmount: BigDecimal,
  coinCurrency: String,
  exchangeRate: BigDecimal,
  eurExchangeRate: BigDecimal,
  createdAt: LocalDateTime,
  expirationTime: LocalDateTime
)

object PaymentResponse {
  def fromDomain(payment: Payment): PaymentResponse = PaymentResponse(
    payment.id.value,
    payment.fiatAmount.value,
    payment.fiatCurrency.value,
    payment.coinAmount.value,
    payment.coinCurrency.value,
    payment.exchangeRate.value,
    payment.eurExchangeRate.value,
    payment.createdAt.value,
    payment.expirationTime.value
  )
}

final case class MultiplePaymentsResponse(
  list: List[PaymentResponse]
)
