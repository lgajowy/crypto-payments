package com.lgajowy

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import cats.kernel.Semigroup
import com.lgajowy.domain.Payment
import com.lgajowy.http.dto.PaymentRequest
import com.lgajowy.persistence.DB

import java.util.UUID

object PaymentsActor {

  sealed trait Command
  final case class GetPayments(currency: String, replyTo: ActorRef[GetPaymentsResponse]) extends Command
  final case class GetPaymentsStats(currency: String, replyTo: ActorRef[GetPaymentsStatsResponse]) extends Command
  final case class CreatePayment(payment: PaymentRequest, replyTo: ActorRef[PaymentCreationResponse]) extends Command
  final case class GetPayment(id: UUID, replyTo: ActorRef[GetPaymentResponse]) extends Command

  final case class GetPaymentResponse(maybePayment: Option[Payment])
  final case class GetPaymentsStatsResponse(paymentCount: Int)
  final case class GetPaymentsResponse(payments: List[Payment])
  final case class PaymentCreationResponse(result: Either[ErrorInfo, Unit])

  def apply(paymentRegistry: PaymentRegistry): Behavior[Command] = registry(paymentRegistry)

  private def registry(paymentRegistry: PaymentRegistry): Behavior[Command] =
    Behaviors.receiveMessage {
      case CreatePayment(request, replyTo) => {
        val creationResult: Either[ErrorInfo, Unit] = paymentRegistry
          .createPayment(request.toDomain())
          .left
          .map(ErrorInfo.fromPaymentErrors)

        replyTo ! PaymentCreationResponse(creationResult)
        Behaviors.same
      }

      case GetPayments(currency, replyTo) =>
        replyTo ! GetPaymentsResponse(DB.selectPaymentsByFiatCurrency(currency))
        Behaviors.same

      case GetPayment(id, replyTo) =>
        replyTo ! GetPaymentResponse(DB.selectPaymentById(id))
        Behaviors.same

      case GetPaymentsStats(currency, replyTo) =>
        val count = DB.countPaymentsByFiatCurrency(currency)
        replyTo ! GetPaymentsStatsResponse(count)
        Behaviors.same
    }
}
