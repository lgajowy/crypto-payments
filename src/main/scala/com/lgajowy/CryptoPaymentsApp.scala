package com.lgajowy

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import pureconfig.generic.auto._
import pureconfig.ConfigSource

import scala.util.{ Failure, Success }

object CryptoPaymentsApp {

  def main(args: Array[String]): Unit = {
    ConfigSource.default
      .load[Configuration]
      .fold(
        // TODO: add better logging
        error => println(error),
        configuration => setupApplication(configuration)
      )
  }

  private def setupApplication(configuration: Configuration): ActorSystem[Nothing] = {
    val rootBehavior = Behaviors.setup[Nothing] { context =>
      println(configuration)
      val paymentRegistryActor = context.spawn(PaymentRegistry(), "PaymentsRegistryActor")
      context.watch(paymentRegistryActor)

      val routes = new PaymentRoutes(configuration.routes, paymentRegistryActor)(context.system)
      startHttpServer(routes.allRoutes)(context.system)

      Behaviors.empty
    }
    ActorSystem[Nothing](rootBehavior, "PaymentRegistryHttpServer")
  }

  private def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {
    import system.executionContext

    val futureBinding = Http().newServerAt("localhost", 8080).bind(routes)
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }
}
