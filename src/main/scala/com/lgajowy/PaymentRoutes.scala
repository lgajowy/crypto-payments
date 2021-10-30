package com.lgajowy

import akka.actor.typed.scaladsl.AskPattern.{Askable, _}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.lgajowy.PaymentsActor.{CreatePayment, GetPayment, GetPayments, GetPaymentsStats}
import com.lgajowy.configuration.RoutesConfiguration
import com.lgajowy.http.dto._
import com.lgajowy.http.endpoints
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.jdk.DurationConverters._

class PaymentRoutes(config: RoutesConfiguration, paymentsActor: ActorRef[PaymentsActor.Command])(
  implicit val system: ActorSystem[_]
) {

  private val interpreter: AkkaHttpServerInterpreter = AkkaHttpServerInterpreter()

  private implicit val timeout: Timeout = Timeout.create(config.askTimeout.toJava)

  def createPayment(request: PaymentRequest): Future[Either[ErrorInfo, Unit]] = {
    paymentsActor
      .ask(CreatePayment(request, _))
      .map(_.result)
  }

  def getPayment(id: UUID): Future[Either[ErrorInfo, PaymentResponse]] = {
    paymentsActor
      .ask(GetPayment(PaymentIdRequest(id), _))
      .map(_.result)
  }

  def getPayments(currency: String): Future[Either[Unit, MultiplePaymentsResponse]] = {
    paymentsActor
      .ask(GetPayments(FiatCurrencyRequest(currency), _))
      .map(response => Right(response.result))
  }

  def getPaymentsStats(currency: String): Future[Either[Unit, PaymentsStatsResponse]] = {
    paymentsActor
      .ask(GetPaymentsStats(FiatCurrencyRequest(currency), _))
      .map(response => Right(response.result))
  }

  val allRoutes: Route = List(
    interpreter.toRoute(endpoints.getPayment)(getPayment),
    interpreter.toRoute(endpoints.postPayment)(createPayment),
    interpreter.toRoute(endpoints.getPayments)(getPayments),
    interpreter.toRoute(endpoints.getPaymentsStats)(getPaymentsStats)
  ).reduce(_ ~ _)
}
