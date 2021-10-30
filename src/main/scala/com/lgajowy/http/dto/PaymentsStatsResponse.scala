package com.lgajowy.http.dto

import com.lgajowy.domain.PaymentStats

case class PaymentsStatsResponse(
  totalPaymentsCount: Int,
  fiatCurrencyPaymentsCount: Int
)

object PaymentsStatsResponse {
  def fromDomain(stats: PaymentStats): PaymentsStatsResponse = PaymentsStatsResponse(
    stats.totalPaymentsCount.value,
    stats.fiatCurrencyPaymentsCount.value
  )
}
