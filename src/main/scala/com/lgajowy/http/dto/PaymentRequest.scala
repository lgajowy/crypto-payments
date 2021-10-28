package com.lgajowy.http.dto

case class PaymentRequest(fiatAmount: BigDecimal, fiatCurrency: String, coinCurrency: String)
