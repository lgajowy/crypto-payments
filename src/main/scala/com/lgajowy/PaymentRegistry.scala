package com.lgajowy

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ ActorRef, Behavior }
import com.lgajowy.domain.Payment
import com.lgajowy.http.dto.PaymentRequest
import com.lgajowy.persistence.DB

import java.util.UUID

object PaymentRegistry {

  sealed trait Command
  final case class GetPayments(currency: String, replyTo: ActorRef[GetPaymentsResponse]) extends Command
  final case class GetPaymentsStats(currency: String, replyTo: ActorRef[GetPaymentsStatsResponse]) extends Command
  final case class CreatePayment(payment: PaymentRequest, replyTo: ActorRef[PaymentCreated]) extends Command
  final case class GetPayment(id: UUID, replyTo: ActorRef[GetPaymentResponse]) extends Command

  final case class GetPaymentResponse(maybePayment: Option[Payment])
  final case class GetPaymentsStatsResponse(paymentCount: Int)
  final case class GetPaymentsResponse(payments: List[Payment])
  final case class PaymentCreated()

  def apply(): Behavior[Command] = registry()

  private def registry(): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetPayments(currency, replyTo) =>
        replyTo ! GetPaymentsResponse(DB.selectPaymentsByFiatCurrency(currency))
        Behaviors.same
      case CreatePayment(request, replyTo) => {
        val payment = Payment(
          UUID.randomUUID(),
          request.fiatAmount,
          request.fiatCurrency,
          0,
          request.coinCurrency
        )

        DB.insertPayment(payment)
        replyTo ! PaymentCreated()
      }
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
