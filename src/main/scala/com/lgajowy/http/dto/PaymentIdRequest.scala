package com.lgajowy.http.dto

import com.lgajowy.domain.PaymentId

import java.util.UUID

case class PaymentIdRequest(value: UUID) {
  def toDomain() = PaymentId(value)
}