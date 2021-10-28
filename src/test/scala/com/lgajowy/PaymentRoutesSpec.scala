package com.lgajowy

import akka.http.scaladsl.model.{ ContentTypes, HttpRequest, MessageEntity, StatusCodes }
import akka.util.Timeout
import com.lgajowy.http.PaymentRoutes
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.{ ActorRef, ActorSystem }
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import com.lgajowy.http.JsonFormats._

class PaymentRoutesSpec extends AnyWordSpec with Matchers with ScalaFutures with ScalatestRouteTest {

  lazy val testKit: ActorTestKit = ActorTestKit()
  implicit def typedSystem: ActorSystem[Nothing] = testKit.system
  override def createActorSystem(): akka.actor.ActorSystem =
    testKit.system.classicSystem

  val paymentRegistry: ActorRef[PaymentRegistry.Command] = testKit.spawn(PaymentRegistry())
  lazy val routes: Route = new PaymentRoutes(paymentRegistry).allRoutes

  private implicit val timeout: Timeout =
    Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))

  "Payment routes" should {
    "return no payments when there is no payments of a given currency in the db" in {
      val request = HttpRequest(uri = "/payments?currency=PLN")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[MultiplePaymentsResponse] should ===(MultiplePaymentsResponse(List()))
      }
    }

    // TODO: This should be returning 404
    "return no payment when there is no payment with the searched UUID" in {
      val request = HttpRequest(uri = "/payment/8ab3e8be-382b-11ec-8d3d-0242ac130003")

      request ~> routes ~> check {
        status should ===(StatusCodes.BadRequest)
      }
    }

    // TODO: Create GenUUID typeclass. Have a production and test interpreter.
    //  Test interpreter will generate deterministically. Thanks to that It will be easier to test cases like below.
    "return previously added payment " in {
      val paymentEntity = Marshal(PaymentRequest(BigDecimal(1), "USD", "BTC")).to[MessageEntity].futureValue
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

  }
}