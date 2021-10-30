package com.lgajowy

import akka.actor.typed.{ ActorRef, ActorSystem }
import akka.actor.typed.scaladsl.AskPattern.{ Askable, _ }
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import cats.data.Validated
import cats.data.Validated.{ Invalid, Valid }
import com.lgajowy.PaymentRegistry.{ CreatePayment, GetPayment, GetPayments, GetPaymentsStats }
import com.lgajowy.domain.{ Payment, PaymentValidationError }
import com.lgajowy.http.dto.{ MultiplePaymentsResponse, PaymentRequest, PaymentResponse, StatsResponse }
import com.lgajowy.http.endpoints
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

import scala.jdk.DurationConverters._
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// TODO: Error handling
class PaymentRoutes(config: RoutesConfiguration, paymentRegistry: ActorRef[PaymentRegistry.Command])(
  implicit val system: ActorSystem[_]
) {

  private val interpreter: AkkaHttpServerInterpreter = AkkaHttpServerInterpreter()

  private implicit val timeout: Timeout = Timeout.create(config.askTimeout.toJava)

  def createPayment(request: PaymentRequest): Future[Either[ErrorInfo, Unit]] = {
    paymentRegistry
      .ask(CreatePayment(request, _))
      .map(_.result match {
        case Invalid(e) => Left(ErrorInfo(e.toString))
        case Valid(_)   => Right(())
      })
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
