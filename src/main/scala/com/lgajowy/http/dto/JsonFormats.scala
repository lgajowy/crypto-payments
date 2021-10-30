package com.lgajowy.http.dto

import spray.json.DefaultJsonProtocol._
import spray.json.{ DeserializationException, JsString, JsValue, JsonFormat, RootJsonFormat }

import java.util.UUID

object JsonFormats {

  implicit object UuidJsonFormat extends JsonFormat[UUID] {
    def write(x: UUID): JsString = JsString(x.toString)
    def read(value: JsValue): UUID = value match {
      case JsString(x) => UUID.fromString(x)
      case x           => throw DeserializationException("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val paymentResponseFormat: RootJsonFormat[PaymentResponse] = jsonFormat1(PaymentResponse.apply)
  implicit val paymentRequestFormat: RootJsonFormat[PaymentRequest] = jsonFormat3(PaymentRequest.apply)
  implicit val multiplePaymentsResponseFormat: RootJsonFormat[MultiplePaymentsResponse] = jsonFormat1(
    MultiplePaymentsResponse.apply
  )
  implicit val statsResponseFormat: RootJsonFormat[PaymentsStatsResponse] = jsonFormat2(PaymentsStatsResponse.apply)
  implicit val errorInfoFormat: RootJsonFormat[ErrorInfo] = jsonFormat1(ErrorInfo.apply)

}
