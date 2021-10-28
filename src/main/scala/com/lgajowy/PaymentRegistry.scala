package com.lgajowy

import java.util.UUID
import scala.concurrent.Future

object PaymentRegistry {

  def createPayment(request: PaymentRequest): Future[Either[Unit, Unit]] = Future.successful(Right(()))

  def getPayment(id: UUID): Future[Either[Unit, PaymentResponse]] =
    Future.successful(Right[Unit, PaymentResponse](PaymentResponse(id)))

  def getPayments(currency: String): Future[Either[Unit, MultiplePaymentsResponse]] =
    Future.successful(Right(MultiplePaymentsResponse(List(PaymentResponse(UUID.randomUUID())))))

  def getPaymentsStats(currency: String): Future[Either[Unit, StatsResponse]] =
    Future.successful(Right(StatsResponse("tbd")))

}
