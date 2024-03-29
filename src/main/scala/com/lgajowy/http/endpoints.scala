package com.lgajowy.http

import com.lgajowy.http.dto.JsonFormats._
import com.lgajowy.http.dto._
import sttp.model.StatusCode
import sttp.model.StatusCode.Created
import sttp.tapir._
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.spray.jsonBody

import java.util.UUID

object endpoints {

  val getPayment: Endpoint[UUID, ErrorInfo, PaymentResponse, Any] =
    endpoint.get
      .in("payment" / path[UUID])
      .errorOut(jsonBody[ErrorInfo])
      .errorOut(statusCode(StatusCode.NotFound))
      .out(jsonBody[PaymentResponse])

  val postPayment: Endpoint[PaymentRequest, ErrorInfo, Unit, Any] =
    endpoint.post
      .in("payment" / "new")
      .in(jsonBody[PaymentRequest])
      .errorOut(jsonBody[ErrorInfo])
      .out(statusCode(Created))

  val getPayments: Endpoint[String, Unit, MultiplePaymentsResponse, Any] =
    endpoint.get
      .in("payments" / query[String]("currency"))
      .out(jsonBody[MultiplePaymentsResponse])

  val getPaymentsStats: Endpoint[String, Unit, PaymentsStatsResponse, Any] =
    endpoint.get
      .in("payments" / "stats" / query[String]("currency"))
      .out(jsonBody[PaymentsStatsResponse])

}
