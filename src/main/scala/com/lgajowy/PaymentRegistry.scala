package com.lgajowy

import java.util.UUID
import scala.concurrent.Future

object PaymentRegistry {
  def createPayment(request: PaymentRequest): Future[Either[Unit, Unit]] = Future.successful(Right(()))

  def getPayment(id: UUID): Future[Either[Unit, PaymentResponse]] =
    Future.successful(Right[Unit, PaymentResponse](PaymentResponse(id)))

}
