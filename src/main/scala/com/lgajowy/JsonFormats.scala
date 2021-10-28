package com.lgajowy

import com.lgajowy.UserRegistry.ActionPerformed
import spray.json.DefaultJsonProtocol._
import spray.json.{ DeserializationException, JsString, JsValue, JsonFormat }

import java.util.UUID

object JsonFormats {

  implicit object UuidJsonFormat extends JsonFormat[UUID] {
    def write(x: UUID): JsString = JsString(x.toString)
    def read(value: JsValue): UUID = value match {
      case JsString(x) => UUID.fromString(x)
      case x           => throw DeserializationException("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val userJsonFormat = jsonFormat3(User)
  implicit val usersJsonFormat = jsonFormat1(Users)
  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
  implicit val paymentResponseFormat = jsonFormat1(PaymentResponse)

}
