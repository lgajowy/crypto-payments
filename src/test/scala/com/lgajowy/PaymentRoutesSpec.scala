package com.lgajowy

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.{ ActorRef, ActorSystem }
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{ ContentTypes, HttpRequest, MessageEntity, StatusCodes }
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.Timeout
import com.lgajowy.configuration.Configuration
import com.lgajowy.http.dto.JsonFormats._
import com.lgajowy.http.dto.{ ErrorInfo, MultiplePaymentsResponse, PaymentRequest, PaymentResponse }
import com.lgajowy.tools.UuidGenerator
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import pureconfig.ConfigSource
import pureconfig.generic.auto._

import java.time.{ Clock, Instant, ZoneOffset }
import java.util.UUID

class PaymentRoutesSpec extends AnyWordSpec with Matchers with ScalaFutures with ScalatestRouteTest {

  lazy val testKit: ActorTestKit = ActorTestKit()
  implicit def typedSystem: ActorSystem[Nothing] = testKit.system
  override def createActorSystem(): akka.actor.ActorSystem =
    testKit.system.classicSystem

  val configuration: Configuration = ConfigSource.default
    .load[Configuration]
    .fold(
      error => throw new RuntimeException(error.toString()),
      config => config
    )

  private val testUuidGenerator: UuidGenerator = () => UUID.fromString("68a6ceae-c52e-4a94-b523-5ac16f7cf627")
  private val testClock: Clock = Clock.fixed(Instant.parse("2018-08-19T16:45:42.00Z"), ZoneOffset.UTC)

  val paymentRegistry: PaymentRegistry =
    PaymentRegistry(configuration.api.payment, Exchange())(testUuidGenerator, testClock)
  val paymentActor: ActorRef[PaymentsActor.Command] = testKit.spawn(PaymentsActor(paymentRegistry))
  lazy val routes: Route = new PaymentRoutes(configuration.routes, paymentActor).allRoutes

  private implicit val timeout: Timeout =
    Timeout.create(system.settings.config.getDuration("routes.ask-timeout"))

  "Payment routes" should {
    "respond with no payments when there is no payments of a given currency in the db" in {
      val request = HttpRequest(uri = "/payments?currency=PLN")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[MultiplePaymentsResponse] should ===(MultiplePaymentsResponse(List()))
      }
    }

    "respond with no payment when there is no payment with the searched UUID" in {
      val request = HttpRequest(uri = "/payment/8ab3e8be-382b-11ec-8d3d-0242ac130003")

      request ~> routes ~> check {
        status should ===(StatusCodes.NotFound)
      }
    }

    "respond with previously added payment " in {
      val paymentEntity = Marshal(PaymentRequest(BigDecimal(30), "USD", "BTC")).to[MessageEntity].futureValue
      val createPaymentRequest = Post("payment/new").withEntity(paymentEntity)
      createPaymentRequest ~> routes ~> check {
        status should ===(StatusCodes.Created)
      }

      val getPayment = HttpRequest(uri = "/payment/68a6ceae-c52e-4a94-b523-5ac16f7cf627")

      getPayment ~> routes ~> check {
        entityAs[PaymentResponse] should ===(PaymentResponse(UUID.fromString("68a6ceae-c52e-4a94-b523-5ac16f7cf627")))
      }

    }

    "not allow creating payment outside of the eur price range" in {
      val paymentEntity =
        Marshal(PaymentRequest(BigDecimal(1), "USD", "unsupportedCrypto")).to[MessageEntity].futureValue
      val createPaymentRequest = Post("payment/new").withEntity(paymentEntity)
      createPaymentRequest ~> routes ~> check {
        status should ===(StatusCodes.BadRequest)
        entityAs[ErrorInfo]
      }
    }
  }
}
