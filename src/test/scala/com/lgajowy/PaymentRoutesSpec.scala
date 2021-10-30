package com.lgajowy

import akka.http.scaladsl.model.{ ContentTypes, HttpRequest, MessageEntity, StatusCodes }
import akka.util.Timeout
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.{ ActorRef, ActorSystem }
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import com.lgajowy.http.dto.JsonFormats._
import com.lgajowy.http.dto.{ MultiplePaymentsResponse, PaymentRequest }
import pureconfig.generic.auto._
import pureconfig.ConfigSource

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


  val paymentRegistry: PaymentRegistry = PaymentRegistry(configuration.api.payment)
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

    // TODO: Create GenUUID typeclass. Have a production and test interpreter.
    //  Test interpreter will generate deterministically. Thanks to that It will be easier to test cases like below.
    "respond with previously added payment " in {
      val paymentEntity = Marshal(PaymentRequest(BigDecimal(30), "USD", "BTC")).to[MessageEntity].futureValue
      val createPaymentRequest = Post("payment/new").withEntity(paymentEntity)
      createPaymentRequest ~> routes ~> check {
        status should ===(StatusCodes.Created)
      }

      val getPaymentsRequest = HttpRequest(uri = "/payments?currency=USD")

      // TODO: This should match the returned (deterministic) id set.
      getPaymentsRequest ~> routes ~> check {
        entityAs[MultiplePaymentsResponse].list.size shouldBe 1
      }

    }

    "not allow creating payment outside of the eur price range" in {
      val paymentEntity = Marshal(PaymentRequest(BigDecimal(1), "unsupportedCrypto", "unsupportedCrypto")).to[MessageEntity].futureValue
      val createPaymentRequest = Post("payment/new").withEntity(paymentEntity)
      createPaymentRequest ~> routes ~> check {
        status should ===(StatusCodes.BadRequest)
        entityAs[ErrorInfo]
      }
    }

    "not allow creating payment with unsupported coin" in {
      val paymentEntity = Marshal(PaymentRequest(BigDecimal(30), "USD", "unsupported")).to[MessageEntity].futureValue
      val createPaymentRequest = Post("payment/new").withEntity(paymentEntity)
      createPaymentRequest ~> routes ~> check {
        status should ===(StatusCodes.BadRequest)
      }
    }

    "not allow creating payment with unsupported fiat currency" in {
      val paymentEntity =
        Marshal(PaymentRequest(BigDecimal(30), "unsupportedFiat", "BTC")).to[MessageEntity].futureValue
      val createPaymentRequest = Post("payment/new").withEntity(paymentEntity)
      createPaymentRequest ~> routes ~> check {
        status should ===(StatusCodes.BadRequest)
      }
    }
  }
}
