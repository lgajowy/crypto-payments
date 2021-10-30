package com.lgajowy.http

import com.lgajowy.ErrorInfo
import com.lgajowy.domain.PaymentError
import sttp.tapir._
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.spray.jsonBody
import com.lgajowy.http.dto.JsonFormats._
import com.lgajowy.http.dto.{MultiplePaymentsResponse, PaymentRequest, PaymentResponse, StatsResponse}
import sttp.model.StatusCode.Created

import java.util.UUID

object endpoints {

  val getPayment: Endpoint[UUID, Unit, PaymentResponse, Any] =
    endpoint.get
      .in("payment" / path[UUID])
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

  val getPaymentsStats: Endpoint[String, Unit, StatsResponse, Any] =
    endpoint.get
      .in("payments" / "stats" / query[String]("currency"))
      .out(jsonBody[StatsResponse])

}
