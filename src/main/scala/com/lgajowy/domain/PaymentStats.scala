package com.lgajowy.domain

case class PaymentStats(
  totalPaymentsCount: TotalPaymentsCount,
  fiatCurrencyPaymentsCount: FiatCurrencyPaymentsCount
)

case class TotalPaymentsCount(value: Int)
case class FiatCurrencyPaymentsCount(value: Int)
