package com.lgajowy.domain

case class PaymentToCreate(fiatAmount: FiatAmount, fiatCurrency: FiatCurrency, coinCurrency: CoinCurrency)
case class ValidatedPaymentToCreate(fiatAmount: FiatAmount, fiatCurrency: FiatCurrency, coinCurrency: CoinCurrency)
