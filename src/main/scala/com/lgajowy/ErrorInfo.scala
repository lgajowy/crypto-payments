package com.lgajowy

import com.lgajowy.domain.PaymentError

case class ErrorInfo(error: String)

object ErrorInfo {

  def fromPaymentError(error: PaymentError): ErrorInfo = {
    ErrorInfo(error.message)
  }

  def fromPaymentErrors(list: List[PaymentError]): ErrorInfo = {
    ErrorInfo(list.map(_.message).mkString(", "))
  }
}
