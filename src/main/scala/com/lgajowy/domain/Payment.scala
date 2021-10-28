package com.lgajowy.domain

import java.util.UUID

final case class Payment(
  id: UUID,
  fiatAmount: BigDecimal,
  fiatCurrency: String,
  coinAmount: BigDecimal,
  coinCurrency: String
)
