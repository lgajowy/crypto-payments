package com.lgajowy

import com.lgajowy.domain.Payment

import java.util.UUID

final case class PaymentResponse(
  id: UUID
)



final case class MultiplePaymentsResponse(
  list: List[PaymentResponse]
)
