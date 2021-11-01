package com.lgajowy.services

import com.lgajowy.domain.{ CoinAmount, CoinCurrency, ExchangeRate, FiatAmount, FiatCurrency }
import com.lgajowy.persistence.MarketDataSource
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ExchangeSpec extends AnyWordSpec with Matchers {

  val ratesSource: MarketDataSource = new MarketDataSource {
    override val exchangeRatesOfBTC: Map[String, BigDecimal] =
      Map("EUR" -> 42000.0, "USD" -> 50000.0)
    override val exchangeRatesToEUR: Map[String, BigDecimal] =
      Map("EUR" -> 1.0, "USD" -> 0.84, "BTC" -> 42000.0)
  }

  "Exchange" should {
    "should change EUR to BTC (provided both are supported)" in {
      val exchange = Exchange(ratesSource)

      val eurAmount = FiatAmount(BigDecimal(84000))
      val eur = FiatCurrency("EUR")
      val btc = CoinCurrency("BTC")

      exchange
        .getCoinExchangeRate(eur, btc)
        .map(rate => {
          exchange.exchangeToCoin(eurAmount, rate) should ===(CoinAmount(BigDecimal(2)))
        })
        .orElse(fail())
    }

    "should fail to return coin rates for unknown coin" in {
      val exchange = Exchange(ratesSource)
      val unknown = CoinCurrency("UNK")
      exchange.getCoinExchangeRate(FiatCurrency("EUR"), unknown) should be(None)
    }

    "should exchange supported value to EUR" in {
      val exchange = Exchange(ratesSource)
      val usd = FiatCurrency("USD")
      val usdAmount = FiatAmount(BigDecimal(100))
      exchange
        .getEurExchangeRate(usd)
        .map(eurRate => exchange.exchangeToFiat(usdAmount, eurRate.rate) should ===(FiatAmount(BigDecimal(84))))
        .orElse(fail())
    }

    "should fail to return fiat rates for unknown fiat currency" in {
      val exchange = Exchange(ratesSource)
      val unknown = FiatCurrency("UNK")
      exchange.getEurExchangeRate(unknown) should be(None)
    }
  }
}
