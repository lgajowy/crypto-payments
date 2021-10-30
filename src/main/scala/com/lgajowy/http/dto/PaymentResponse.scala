package com.lgajowy.http.dto

import com.lgajowy.domain.Payment

import java.util.UUID

final case class PaymentResponse(
  id: UUID
)

object PaymentResponse{
  def fromDomain(payment: Payment): PaymentResponse = PaymentResponse(payment.id)
}

final case class MultiplePaymentsResponse(
  list: List[PaymentResponse]
)
