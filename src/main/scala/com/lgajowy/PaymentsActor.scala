package com.lgajowy

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import com.lgajowy.http.dto._
import com.lgajowy.services.PaymentRegistry

object PaymentsActor {

  sealed trait Command
  final case class GetPayments(currency: FiatCurrencyRequest, replyTo: ActorRef[GetPaymentsResponse]) extends Command
  final case class GetPaymentsStats(currency: FiatCurrencyRequest, replyTo: ActorRef[GetPaymentsStatsResponse])
    extends Command
  final case class CreatePayment(payment: PaymentRequest, replyTo: ActorRef[PaymentCreationResponse]) extends Command
  final case class GetPayment(id: PaymentIdRequest, replyTo: ActorRef[GetPaymentResponse]) extends Command

  final case class GetPaymentResponse(result: Either[ErrorInfo, PaymentResponse])
  final case class GetPaymentsStatsResponse(result: PaymentsStatsResponse)
  final case class GetPaymentsResponse(result: MultiplePaymentsResponse)
  final case class PaymentCreationResponse(result: Either[ErrorInfo, Unit])

  def apply(paymentRegistry: PaymentRegistry): Behavior[Command] = registry(paymentRegistry)

  private def registry(paymentRegistry: PaymentRegistry): Behavior[Command] =
    Behaviors.receiveMessage {
      case CreatePayment(request, replyTo) =>
        val creationResult: Either[ErrorInfo, Unit] = paymentRegistry
          .createPayment(request.toDomain())
          .left
          .map(ErrorInfo.fromPaymentErrors)

        replyTo ! PaymentCreationResponse(creationResult)
        Behaviors.same

      case GetPayments(currency, replyTo) =>
        val payments: List[PaymentResponse] =
          paymentRegistry.getPayments(currency.toDomain()).map(PaymentResponse.fromDomain)
        replyTo ! GetPaymentsResponse(MultiplePaymentsResponse(payments))
        Behaviors.same

      case GetPayment(id, replyTo) =>
        val searchResult: Either[ErrorInfo, PaymentResponse] = paymentRegistry
          .findPayment(id.toDomain())
          .map(PaymentResponse.fromDomain)
          .left
          .map(ErrorInfo.fromPaymentError)

        replyTo ! GetPaymentResponse(searchResult)
        Behaviors.same

      case GetPaymentsStats(currency, replyTo) =>
        val stats: PaymentsStatsResponse =
          PaymentsStatsResponse.fromDomain(paymentRegistry.getPaymentStats(currency.toDomain()))

        replyTo ! GetPaymentsStatsResponse(stats)
        Behaviors.same
    }
}
