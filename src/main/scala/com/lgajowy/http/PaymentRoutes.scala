package com.lgajowy.http

import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.lgajowy.PaymentRegistry.{ CreatePayment, GetPayment, GetPayments, GetPaymentsStats }
import com.lgajowy.{ MultiplePaymentsResponse, PaymentRegistry, PaymentRequest, PaymentResponse, StatsResponse }
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import com.lgajowy.domain.Payment

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// TODO: Error handling
class PaymentRoutes(paymentRegistry: ActorRef[PaymentRegistry.Command])(implicit val system: ActorSystem[_]) {

  private val interpreter: AkkaHttpServerInterpreter = AkkaHttpServerInterpreter()

  private implicit val timeout: Timeout =
    Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))

  def createPayment(request: PaymentRequest): Future[Either[Unit, Unit]] = {
    paymentRegistry.ask(CreatePayment(request, _)).map(_ => Right(()))
  }

  def getPayment(id: UUID): Future[Either[Unit, PaymentResponse]] = {
    paymentRegistry
      .ask(GetPayment(id, _))
      .flatMap(response => Future.successful(response.maybePayment.map(toPaymentResponse).toRight(())))
  }

  def getPayments(currency: String): Future[Either[Unit, MultiplePaymentsResponse]] = {
    paymentRegistry
      .ask(GetPayments(currency, _))
      .flatMap(
        (response: PaymentRegistry.GetPaymentsResponse) =>
          Future.successful(Right(MultiplePaymentsResponse(response.payments.map(toPaymentResponse))))
      )
  }

  private def toPaymentResponse(payment: Payment): PaymentResponse = PaymentResponse(payment.id)

  def getPaymentsStats(currency: String): Future[Either[Unit, StatsResponse]] = {
    paymentRegistry
      .ask(GetPaymentsStats(currency, _))
      .flatMap(
        (response: PaymentRegistry.GetPaymentsStatsResponse) =>
          Future.successful(Right(StatsResponse(response.paymentCount)))
      )
  }

  val allRoutes: Route = List(
    interpreter.toRoute(endpoints.getPayment)(getPayment),
    interpreter.toRoute(endpoints.postPayment)(createPayment),
    interpreter.toRoute(endpoints.getPayments)(getPayments),
    interpreter.toRoute(endpoints.getPaymentsStats)(getPaymentsStats)
  ).reduce(_ ~ _)
}
