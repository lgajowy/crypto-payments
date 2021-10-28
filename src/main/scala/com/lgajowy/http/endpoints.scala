package com.lgajowy.http

import com.lgajowy.{ MultiplePaymentsResponse, PaymentRequest, PaymentResponse, StatsResponse }
import sttp.tapir._
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.spray.jsonBody
import JsonFormats._
import sttp.model.StatusCode.Created

import java.util.UUID

object endpoints {

  val getPayment: Endpoint[UUID, Unit, PaymentResponse, Any] =
    endpoint.get
      .in("payment" / path[UUID])
      .out(jsonBody[PaymentResponse])

  val postPayment: Endpoint[PaymentRequest, Unit, Unit, Any] =
    endpoint.post
      .in("payment" / "new")
      .in(jsonBody[PaymentRequest])
      .out(statusCode(Created))

  val getPayments: Endpoint[String, Unit, MultiplePaymentsResponse, Any] =
    endpoint.get
      .in("payments" / query[String]("currency"))
      .out(jsonBody[MultiplePaymentsResponse])

  val getPaymentsStats: Endpoint[String, Unit, StatsResponse, Any] =
    endpoint.get
      .in("payments" / "stats" / query[String]("currency"))
      .out(jsonBody[StatsResponse])

}
