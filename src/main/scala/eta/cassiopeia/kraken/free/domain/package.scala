package eta.cassiopeia.kraken.free

import eta.cassiopeia.kraken.free.domain.BuyOrSell.BuyOrSell
import eta.cassiopeia.kraken.free.domain.OrderStatus
import eta.cassiopeia.kraken.free.domain.OrderStatus.OrderStatus
import eta.cassiopeia.kraken.free.domain.OrderType.OrderType
import io.circe.{Decoder, DecodingFailure}

package object domain {
  case class Response[T](error: List[String], result: Option[T])

  object Response {
    implicit def decodeResponse[A: Decoder]: Decoder[Response[A]] =
      Decoder.instance { c =>
        for {
          error <- c.downField("error").as[List[String]]
          result <- c.downField("result").as[Option[A]]
        } yield Response(error, result)
      }
  }

  case class ServerTime(unixTime: Long, rfc1123: String)

  object ServerTime {
    implicit val decodeServerTime: Decoder[ServerTime] = Decoder.instance { c =>
      for {
        unixTime <- c.downField("unixtime").as[Long]
        rfc1123 <- c.downField("rfc1123").as[String]
      } yield ServerTime(unixTime, rfc1123)
    }
  }

  case class Asset(aClass: String,
                   altName: String,
                   decimals: Int,
                   displayDecimals: Int)

  case object Asset {
    implicit val decodeAsset: Decoder[Asset] = Decoder.instance { c =>
      for {
        aClass <- c.downField("aclass").as[String]
        altName <- c.downField("altname").as[String]
        decimals <- c.downField("decimals").as[Int]
        displayDecimals <- c.downField("display_decimals").as[Int]
      } yield Asset(aClass, altName, decimals, displayDecimals)
    }
  }

  case class AssetPair(altName: String,
                       aClassBase: String,
                       base: String,
                       aClassQuote: String,
                       quote: String,
                       lot: String,
                       pairDecimals: Int,
                       lotDecimals: Int,
                       lotMultiplier: Int,
                       leverageBuy: List[Int],
                       leverageSell: List[Int],
                       fees: List[List[Double]],
                       feesMaker: Option[List[List[Double]]],
                       feeVolumeCurrency: String,
                       marginCall: Int,
                       marginStop: Int)

  object AssetPair {
    implicit val decodeAssetPair: Decoder[AssetPair] = Decoder.instance { c =>
      for {
        altName <- c.downField("altname").as[String]
        aClassBase <- c.downField("aclass_base").as[String]
        base <- c.downField("base").as[String]
        aClassQuote <- c.downField("aclass_quote").as[String]
        quote <- c.downField("quote").as[String]
        lot <- c.downField("lot").as[String]
        pairDecimals <- c.downField("pair_decimals").as[Int]
        lotDecimals <- c.downField("lot_decimals").as[Int]
        lotMultiplier <- c.downField("lot_multiplier").as[Int]
        leverageBuy <- c.downField("leverage_buy").as[List[Int]]
        leverageSell <- c.downField("leverage_sell").as[List[Int]]
        fees <- c.downField("fees").as[List[List[Double]]]
        feesMaker <- c.downField("fees_maker").as[Option[List[List[Double]]]]
        feeVolumeCurrency <- c.downField("fee_volume_currency").as[String]
        marginCall <- c.downField("margin_call").as[Int]
        marginStop <- c.downField("margin_stop").as[Int]
      } yield
        AssetPair(
          altName,
          aClassBase,
          base,
          aClassQuote,
          quote,
          lot,
          pairDecimals,
          lotDecimals,
          lotMultiplier,
          leverageBuy,
          leverageSell,
          fees,
          feesMaker,
          feeVolumeCurrency,
          marginCall,
          marginStop
        )
    }
  }

  case class Ticker(askArray: List[String],
                    bidArray: List[String],
                    lastTradeClosed: List[String],
                    volume: List[String],
                    volumeWeightedAveragePrice: List[String],
                    numberOfTrades: List[Int],
                    low: List[String],
                    high: List[String],
                    openingPrice: String)

  object Ticker {
    implicit val decodeTicker: Decoder[Ticker] = Decoder.instance { c =>
      for {
        askArray <- c.downField("a").as[List[String]]
        bidArray <- c.downField("b").as[List[String]]
        lastTradeClosed <- c.downField("c").as[List[String]]
        volume <- c.downField("v").as[List[String]]
        volumeWeightedAveragePrice <- c.downField("p").as[List[String]]
        numberOfTrades <- c.downField("t").as[List[Int]]
        low <- c.downField("l").as[List[String]]
        high <- c.downField("h").as[List[String]]
        openingPrice <- c.downField("o").as[String]
      } yield
        Ticker(askArray,
               bidArray,
               lastTradeClosed,
               volume,
               volumeWeightedAveragePrice,
               numberOfTrades,
               low,
               high,
               openingPrice)
    }
  }

  case class DataWithTime[T](data: Map[String, Seq[T]], timeStamp: Long)

  object DataWithTime {
    implicit def decodeDataWithTime[T: Decoder]: Decoder[DataWithTime[T]] =
      Decoder.instance { c =>
        val key = c.keys.get.toList.head
        for {
          data <- c.downField(key).as[List[T]]
          timeStamp <- c.downField("last").as[Long]
        } yield DataWithTime(Map(key -> data), timeStamp)
      }
  }

  case class OHLC(time: Long,
                  open: String,
                  high: String,
                  low: String,
                  close: String,
                  vwap: String,
                  volume: String,
                  count: Int)

  object OHLC {
    implicit val decodeOHLC: Decoder[OHLC] = Decoder.instance { c =>
      c.focus.flatMap(_.asArray) match {
        case Some(
            fnTime +: fnOpen +: fnHigh +: fnLow +: fnClose +: fnVwap +: fnVolume +: fnCount +: _) =>
          for {
            time <- fnTime.as[Long]
            open <- fnOpen.as[String]
            high <- fnHigh.as[String]
            low <- fnLow.as[String]
            close <- fnClose.as[String]
            vwap <- fnVwap.as[String]
            volume <- fnVolume.as[String]
            count <- fnCount.as[Int]
          } yield OHLC(time, open, high, low, close, vwap, volume, count)
        case None => Left(DecodingFailure("OHLC", c.history))
      }
    }
  }

  case class BookEntry(price: String, volume: String, timestamp: Long)

  object BookEntry {
    implicit val decodeBookEntry: Decoder[BookEntry] = Decoder.instance { c =>
      c.focus.flatMap(_.asArray) match {
        case Some(fnPrice +: fnVolume +: fnTimestamp +: _) =>
          for {
            price <- fnPrice.as[String]
            volume <- fnVolume.as[String]
            timestamp <- fnTimestamp.as[Long]
          } yield BookEntry(price, volume, timestamp)
        case None => Left(DecodingFailure("BookEntry", c.history))
      }
    }
  }

  case class AsksAndBids(asks: Seq[BookEntry], bids: Seq[BookEntry])

  object AsksAndBids {
    implicit val decodeAsksAndBids: Decoder[AsksAndBids] = {
      Decoder.forProduct2("asks", "bids")(AsksAndBids.apply)
    }
  }

  case class RecentTrade(price: String,
                         volume: String,
                         time: Double,
                         buyOrSell: String,
                         orderType: String,
                         miscellaneous: String)

  object RecentTrade {
    implicit val decodeRecentTrade: Decoder[RecentTrade] = Decoder.instance {
      c =>
        c.focus.flatMap(_.asArray) match {
          case Some(
              fnPrice +: fnVolume +: fnTime +: fnBuyOrSell +: fnOrderType +: fnMiscellaneous +: _) =>
            for {
              price <- fnPrice.as[String]
              volume <- fnVolume.as[String]
              time <- fnTime.as[Double]
              buyOrSell <- fnBuyOrSell.as[String]
              orderType <- fnOrderType.as[String]
              miscellaneous <- fnMiscellaneous.as[String]
            } yield
              RecentTrade(price,
                          volume,
                          time,
                          buyOrSell,
                          orderType,
                          miscellaneous)
          case None => Left(DecodingFailure("RecentTrade", c.history))
        }
    }
  }

  case class RecentSpread(time: Long, bid: String, ask: String)

  object RecentSpread {
    implicit val decodeRecentSpread: Decoder[RecentSpread] = Decoder.instance {
      c =>
        c.focus.flatMap(_.asArray) match {
          case Some(fnTime +: fnBid +: fnAsk +: _) =>
            for {
              time <- fnTime.as[Long]
              bid <- fnBid.as[String]
              ask <- fnAsk.as[String]
            } yield RecentSpread(time, bid, ask)
          case None => Left(DecodingFailure("RecentSpread", c.history))
        }
    }
  }

  case class TradeBalance(equivalentBalance: String,
                          tradeBalance: String,
                          marginAmount: String,
                          unrealizedNetProfit: String,
                          costBasis: String,
                          floatingValuation: String,
                          equity: String,
                          freeMargin: String,
                          marginLevel: Option[String])

  object TradeBalance {
    implicit val decodeTradeBalance: Decoder[TradeBalance] = Decoder.instance {
      c =>
        for {
          equivalentBalance <- c.downField("eb").as[String]
          tradeBalance <- c.downField("tb").as[String]
          marginAmount <- c.downField("m").as[String]
          unrealizedNetProfit <- c.downField("n").as[String]
          costBasis <- c.downField("c").as[String]
          floatingValuation <- c.downField("v").as[String]
          equity <- c.downField("e").as[String]
          freeMargin <- c.downField("mf").as[String]
          marginLevel <- c.downField("ml").as[Option[String]]
        } yield
          TradeBalance(
            equivalentBalance,
            tradeBalance,
            marginAmount,
            unrealizedNetProfit,
            costBasis,
            floatingValuation,
            equity,
            freeMargin,
            marginLevel
          )
    }
  }

  object OrderStatus extends Enumeration {
    type OrderStatus = Value
    val pending, open, closed, canceled, expired = Value

    implicit val decodeOderStatus: Decoder[OrderStatus.Value] =
      Decoder.enumDecoder(OrderStatus)
  }

  object BuyOrSell extends Enumeration {
    type BuyOrSell = Value
    val buy, sell = Value

    implicit val decodeBuyOrSell: Decoder[BuyOrSell.Value] =
      Decoder.enumDecoder(BuyOrSell)
  }

  object OrderType extends Enumeration {
    type OrderType = Value
    val market, limit, stop_loss, take_profit, stop_loss_profit,
    stop_loss_profit_limit, stop_loss_limit, take_profit_limit, trailing_stop,
    trailing_stop_limit, stop_loss_and_limit, settle_position = Value

    implicit val decodeOrderType: Decoder[OrderType.Value] =
      Decoder.enumDecoder(OrderType)
  }

  case class OrderDescription(pair: String,
                              buyOrSell: BuyOrSell,
                              orderType: OrderType,
                              price: String,
                              price2: String,
                              leverage: String,
                              order: String)

  object OrderDescription {
    implicit val decodeOrder: Decoder[OrderDescription] =
      Decoder.forProduct7("pair",
                          "type",
                          "ordertype",
                          "price",
                          "price2",
                          "leverage",
                          "order")(OrderDescription.apply)
  }

  case class Order(referralTransactionId: Option[String],
                   userReferenceId: Option[Long],
                   status: OrderStatus,
                   timestamp: Double,
                   startTime: Double,
                   expireTime: Double,
                   description: OrderDescription,
                   volume: String,
                   volumeExecuted: String,
                   cost: String,
                   fee: String,
                   averagePrice: String,
                   stopPrice: Option[String],
                   limitPrice: Option[String],
                   misc: String,
                   orderFlags: String,
                   trades: Option[List[String]])

  object Order {
    implicit val decodeOrder: Decoder[Order] = Decoder.forProduct17(
      "refid",
      "userref",
      "status",
      "opentm",
      "starttm",
      "expiretm",
      "descr",
      "vol",
      "vol_exec",
      "cost",
      "fee",
      "price",
      "stopprice",
      "limitprice",
      "misc",
      "oflags",
      "trades"
    )(Order.apply)
  }

  case class OpenOrder(open: Option[Map[String, Order]])

  object OpenOrder {
    implicit val decodeOpenOrder: Decoder[OpenOrder] =
      Decoder.forProduct1("open")(OpenOrder.apply)
  }

  object CloseTime extends Enumeration {
    type CloseTime = Value
    val open, close, both = Value

    implicit val decodeCloseTime: Decoder[CloseTime.Value] =
      Decoder.enumDecoder(CloseTime)
  }

  case class ClosedOrder(closed: Option[Map[String, Order]], count: Int)

  object ClosedOrder {
    implicit val decodeClosedOrder: Decoder[ClosedOrder] =
      Decoder.forProduct2("closed", "count")(ClosedOrder.apply)
  }

  object TradeType extends Enumeration {
    type PositionType = Value
    //FixMe
    /**
      * type = type of trade (optional)
      * all = all types (default)
      * any position = any position (open or closed)
      * closed position = positions that have been closed
      * closing position = any trade closing all or part of a position
      * no position = non-positional trades
      */
    val all, any, closed, closing, no = Value

    implicit val decodeTradeType: Decoder[TradeType.Value] =
      Decoder.enumDecoder(TradeType)
  }

  case class Trade(ordertxid: String,
                   pair: String,
                   time: Double,
                   buyOrSell: BuyOrSell,
                   orderType: OrderType,
                   price: String,
                   cost: String,
                   fee: String,
                   vol: String,
                   margin: String,
                   misc: String,
                   posstatus: Option[String],
                   cprice: Option[String],
                   ccost: Option[String],
                   cfee: Option[String],
                   cvol: Option[String],
                   cmargin: Option[String],
                   net: Option[String],
                   trades: Option[List[String]])

  object Trade {
    implicit val decodeTrade: Decoder[Trade] =
      Decoder.forProduct19(
        "ordertxid",
        "pair",
        "time",
        "type",
        "ordertype",
        "price",
        "cost",
        "fee",
        "vol",
        "margin",
        "misc",
        "posstatus",
        "cprice",
        "ccost",
        "cfee",
        "cvol",
        "cmargin",
        "net",
        "trades"
      )(Trade.apply)
  }

  case class TradeHistory(trades: Map[String, Trade], count: Int)

  object TradeHistory {
    implicit val decodeTradeHistory: Decoder[TradeHistory] =
      Decoder.forProduct2("trades", "count")(TradeHistory.apply)
  }
}
