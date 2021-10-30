package com.lgajowy.http.dto

import com.lgajowy.ErrorInfo
import spray.json.DefaultJsonProtocol._
import spray.json.{DeserializationException, JsString, JsValue, JsonFormat, RootJsonFormat}

import java.util.UUID

object JsonFormats {

  implicit object UuidJsonFormat extends JsonFormat[UUID] {
    def write(x: UUID): JsString = JsString(x.toString)
    def read(value: JsValue): UUID = value match {
      case JsString(x) => UUID.fromString(x)
      case x           => throw DeserializationException("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val paymentResponseFormat = jsonFormat1(PaymentResponse)
  implicit val paymentRequestFormat = jsonFormat3(PaymentRequest)
  implicit val multiplePaymentsResponseFormat = jsonFormat1(MultiplePaymentsResponse)
  implicit val statsResponseFormat: RootJsonFormat[StatsResponse] = jsonFormat1(StatsResponse)
  implicit val errorInfoFormat: RootJsonFormat[ErrorInfo] = jsonFormat1(ErrorInfo.apply)

}
