package com.lgajowy.http

import akka.http.scaladsl.server.Route
import com.lgajowy.PaymentRegistry
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter
import akka.http.scaladsl.server.Directives._

object Routing {
  private val interpreter: AkkaHttpServerInterpreter = AkkaHttpServerInterpreter()
  private val getPayment: Route = interpreter.toRoute(endpoints.getPayment)(PaymentRegistry.getPayment)
  private val createPayment: Route = interpreter.toRoute(endpoints.postPayment)(PaymentRegistry.createPayment)

  val allRoutes: Route = createPayment ~ getPayment
}
