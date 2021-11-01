package com.lgajowy.services

import com.lgajowy.configuration.PaymentConfig
import com.lgajowy.domain.{ PaymentId, _ }
import com.lgajowy.persistence.{ Database, MarketDataSource }
import com.lgajowy.tools.UuidGenerator
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.{ Clock, Instant, LocalDateTime, ZoneOffset }
import java.util.UUID
import scala.concurrent.duration.FiniteDuration
import org.scalatest.EitherValues._
import org.scalatest.Inside.inside

class PaymentRegistrySpec extends AnyWordSpec with Matchers {

  private val testClock = Clock.fixed(Instant.parse("2018-08-19T16:45:42.00Z"), ZoneOffset.UTC)

  private val uuidGenerator = new UuidGenerator {
    override def generate(): UUID = UUID.randomUUID()
  }

  private val databaseStub: Database = new Database {
    override def insertPayment(payment: Payment): Unit = ()
    override def selectPaymentsByFiatCurrency(currency: FiatCurrency): List[Payment] = List()
    override def selectPaymentById(id: PaymentId): Option[Payment] = None
    override def countAllPayments(): TotalPaymentsCount = TotalPaymentsCount(0)
    override def countPaymentsForFiatCurrency(fiatCurrency: FiatCurrency): FiatCurrencyPaymentsCount =
      FiatCurrencyPaymentsCount(0)
    override def selectSupportedCryptoCurrencies(): List[CoinCurrency] = List()
    override def selectSupportedFiatCurrencies(): List[FiatCurrency] = List()
  }

  private val database: Database = Database.make()
  private val config: PaymentConfig = PaymentConfig(FiniteDuration(5, "minutes"), 1, 100)
  private val exchange: Exchange = Exchange(MarketDataSource.make())

  private val paymentRegistry = PaymentRegistry(
    config,
    exchange,
    database
  )(uuidGenerator, testClock)

  "payment registry" should {
    "create payment properly" in {
      val result: Either[List[PaymentRequestValidationError], Unit] = paymentRegistry.createPayment(
        PaymentToCreate(
          FiatAmount(100),
          FiatCurrency("USD"),
          CoinCurrency("BTC")
        )
      )
      result shouldBe Right(())
    }

    "fail to even start validation when the fiat currency is not found" in {
      val unknownCurrency = FiatCurrency("PLN")

      val result: Either[List[PaymentRequestValidationError], Unit] = paymentRegistry.createPayment(
        PaymentToCreate(
          FiatAmount(100),
          unknownCurrency,
          CoinCurrency("BTC")
        )
      )
      result shouldBe Left(List(UnknownFiatCurrencyExchangeRate(unknownCurrency)))
    }

    "return validation errors if we could change fiat amount to EUR but other problems occurred" in {
      val unsupportedFiatCurrency = FiatCurrency("USD")
      val unsupportedCoinCurrency = CoinCurrency("BTC")

      val registryToTest = PaymentRegistry(config, exchange, databaseStub)(uuidGenerator, testClock)

      val amount = FiatAmount(0)
      val result: Either[List[PaymentRequestValidationError], Unit] = registryToTest.createPayment(
        PaymentToCreate(
          amount,
          unsupportedFiatCurrency,
          unsupportedCoinCurrency
        )
      )
      result.left.value shouldBe List(
        OutOfEURPriceRange(amount, unsupportedFiatCurrency),
        UnsupportedFiatCurrency(unsupportedFiatCurrency),
        UnsupportedCryptoCurrency(unsupportedCoinCurrency)
      )
    }

    "get a payment by UUID" in {
      val uuidValue = UUID.fromString("d183e942-a5ee-4c5f-9acb-34ac9aa40c08")
      val customGenerator = new UuidGenerator {
        override def generate(): UUID = uuidValue
      }

      val registryToTest = PaymentRegistry(config, exchange, database)(customGenerator, testClock)
      registryToTest.createPayment(PaymentToCreate(FiatAmount(100), FiatCurrency("USD"), CoinCurrency("BTC")))

      val paymentId = PaymentId(uuidValue)
      val result: Either[PaymentNotFound, Payment] = registryToTest.findPayment(paymentId)

      inside(result) { case Right(payment) => payment.id should ===(paymentId) }
    }
  }
}
