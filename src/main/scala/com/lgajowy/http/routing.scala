package com.lgajowy.http

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.lgajowy.PaymentRegistry
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

object routing {
  private val interpreter: AkkaHttpServerInterpreter = AkkaHttpServerInterpreter()

  val routes: Route = List(
    interpreter.toRoute(endpoints.getPayment)(PaymentRegistry.getPayment),
    interpreter.toRoute(endpoints.postPayment)(PaymentRegistry.createPayment),
    interpreter.toRoute(endpoints.getPayments)(PaymentRegistry.getPayments),
    interpreter.toRoute(endpoints.getPaymentsStats)(PaymentRegistry.getPaymentsStats)
  ).reduce(_ ~ _)
}
