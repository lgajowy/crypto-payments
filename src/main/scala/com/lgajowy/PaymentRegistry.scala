package com.lgajowy

import java.util.UUID
import scala.concurrent.Future

object PaymentRegistry {

  def getPayment(id: UUID): Future[Either[Unit, PaymentResponse]] =
    Future.successful(Right[Unit, PaymentResponse](PaymentResponse(id)))

}
