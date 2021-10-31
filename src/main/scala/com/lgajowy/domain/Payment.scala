package com.lgajowy.domain

import java.time.LocalDateTime
import java.util.UUID

final case class Payment(
  id: PaymentId,
  fiatAmount: FiatAmount,
  fiatCurrency: FiatCurrency,
  coinAmount: CoinAmount,
  coinCurrency: CoinCurrency,
  createdAt: CreatedAt,
  expirationTime: ExpirationTime
)

case class PaymentId(value: UUID)
case class FiatAmount(value: BigDecimal)
case class FiatCurrency(value: String)
case class CoinAmount(value: BigDecimal)
case class CoinCurrency(value: String)
case class CreatedAt(value: LocalDateTime)
case class ExpirationTime(value: LocalDateTime)
