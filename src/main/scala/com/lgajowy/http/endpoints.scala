package com.lgajowy.http

import com.lgajowy.{ JsonFormats, PaymentRequest, PaymentResponse }
import sttp.tapir.{ Endpoint, endpoint, path, statusCode }
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.spray.jsonBody
import JsonFormats._
import sttp.model.StatusCode.Created

import java.util.UUID

object endpoints {

  private val payment: Endpoint[Unit, Unit, Unit, Any] = endpoint.in("payment")

  val getPayment: Endpoint[UUID, Unit, PaymentResponse, Any] =
    payment.get
      .in(path[UUID])
      .out(jsonBody[PaymentResponse])

  val postPayment: Endpoint[PaymentRequest, Unit, Unit, Any] = payment.post
    .in("new")
    .in(jsonBody[PaymentRequest])
    .out(statusCode(Created))

}
