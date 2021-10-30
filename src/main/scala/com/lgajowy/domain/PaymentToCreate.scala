package com.lgajowy.domain

case class PaymentToCreate(fiatAmount: BigDecimal, fiatCurrency: String, coinCurrency: String)
case class ValidatedPaymentToCreate(fiatAmount: BigDecimal, fiatCurrency: String, coinCurrency: String)
