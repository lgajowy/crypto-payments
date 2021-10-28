package com.lgajowy

import java.util.UUID

final case class PaymentResponse(
  id: UUID
)

final case class MultiplePaymentsResponse(
  list: List[PaymentResponse]
)
