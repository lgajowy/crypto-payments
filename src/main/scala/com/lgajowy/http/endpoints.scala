package com.lgajowy.http

import com.lgajowy.{JsonFormats, PaymentResponse}
import sttp.tapir.{Endpoint, endpoint, path}

import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.spray.jsonBody
import JsonFormats._

import java.util.UUID

object endpoints {

  val getPayment: Endpoint[UUID, Unit, PaymentResponse, Any] =
    endpoint
      .in("payments")
      .in(path[UUID])
      .out(jsonBody[PaymentResponse])
}
