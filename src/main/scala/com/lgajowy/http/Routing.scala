package com.lgajowy.http

import akka.http.scaladsl.server.Route
import com.lgajowy.PaymentRegistry
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

object Routing {
  val getPayment: Route = AkkaHttpServerInterpreter().toRoute(endpoints.getPayment)(PaymentRegistry.getPayment)
}
