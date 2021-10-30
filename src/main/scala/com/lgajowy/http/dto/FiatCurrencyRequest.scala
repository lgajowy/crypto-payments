package com.lgajowy.http.dto

import com.lgajowy.domain.FiatCurrency

case class FiatCurrencyRequest(value: String) {

  def toDomain() = FiatCurrency(value)
}
