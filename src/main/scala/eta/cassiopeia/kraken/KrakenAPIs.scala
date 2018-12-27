package eta.cassiopeia.kraken

import eta.cassiopeia.kraken.KrakenResponses.{KrakenIO, KrakenResponse}
import eta.cassiopeia.kraken.app.KrakenOp
import eta.cassiopeia.kraken.free.algebra.{PrivateOps, PublicOps}
import eta.cassiopeia.kraken.free.domain.CloseTime.CloseTime
import eta.cassiopeia.kraken.free.domain.LedgerType.LedgerType
import eta.cassiopeia.kraken.free.domain.TradeType.TradeType
import eta.cassiopeia.kraken.free.domain._

class KrakenPublicAPI()(implicit O: PublicOps[KrakenOp]) {
  def getServerTime(): KrakenIO[KrakenResponse[ServerTime]] = O.getServerTime

  def getAssetInfo(info: Option[String] = None,
                   aclass: Option[String] = None,
                   asset: Option[List[String]] = None)
    : KrakenIO[KrakenResponse[Map[String, Asset]]] =
    O.getAssetInfo(info, aclass, asset)

  def getAssetPair(pair: Option[List[(String, String)]] = None)
    : KrakenIO[KrakenResponse[Map[String, AssetPair]]] = O.getAssetPairs(pair)

  def getTickerInformation(pair: List[(String, String)])
    : KrakenIO[KrakenResponse[Map[String, Ticker]]] =
    O.getTickerInformation(pair)

  def getOHLCdata(currency: String,
                  respectToCurrency: String,
                  interval: Option[Int] = None,
                  timeStamp: Option[Long] = None)
    : KrakenIO[KrakenResponse[DataWithTime[OHLC]]] =
    O.getOHLCdata(currency, respectToCurrency, interval, timeStamp)

  def getOrderBook(currency: String,
                   respectToCurrency: String,
                   count: Option[Int] = None)
    : KrakenIO[KrakenResponse[Map[String, AsksAndBids]]] =
    O.getOrderBook(currency, respectToCurrency, count)

  def getRecentTrades(currency: String,
                      respectToCurrency: String,
                      timeStamp: Option[Long] = None)
    : KrakenIO[KrakenResponse[DataWithTime[RecentTrade]]] =
    O.getRecentTrades(currency, respectToCurrency, timeStamp)

  def getRecentSpreadData(currency: String,
                          respectToCurrency: String,
                          timeStamp: Option[Long] = None)
    : KrakenIO[KrakenResponse[DataWithTime[RecentSpread]]] =
    O.getRecentSpreadData(currency, respectToCurrency, timeStamp)

}

class KrakenPrivateAPI()(implicit O: PrivateOps[KrakenOp]) {
  def getAccountBalance(): KrakenIO[KrakenResponse[Map[String, String]]] =
    O.getAccountBalance

  def getTradeBalance(
      aClass: Option[String] = None,
      asset: Option[String] = None): KrakenIO[KrakenResponse[TradeBalance]] =
    O.getTradeBalance(aClass, asset)

  def getOpenOrders(
      trades: Option[Boolean] = Some(false),
      userRef: Option[String] = None): KrakenIO[KrakenResponse[OpenOrder]] =
    O.getOpenOrders(trades, userRef)

  def getClosedOrders(trades: Option[Boolean] = Some(false),
                      userRef: Option[String] = None,
                      start: Option[Long] = None,
                      end: Option[Long] = None,
                      offset: Option[Int] = None,
                      closeTime: Option[CloseTime] = None)
    : KrakenIO[KrakenResponse[ClosedOrder]] =
    O.getClosedOrders(trades, userRef, start, end, offset, closeTime)

  def queryOrders(transactionId: Vector[String],
                  trades: Option[Boolean] = Some(false),
                  userRef: Option[String] = None,
  ): KrakenIO[KrakenResponse[Map[String, Order]]] =
    O.queryOrders(transactionId, trades, userRef)

  def getTradesHistory(
      positionType: Option[TradeType] = None,
      trades: Option[Boolean] = Some(false),
      start: Option[Long] = None,
      end: Option[Long] = None,
      offset: Option[Int] = None): KrakenIO[KrakenResponse[TradeHistory]] =
    O.getTradesHistory(positionType, trades, start, end, offset)

  def queryTrades(transactionId: Vector[String],
                  trades: Option[Boolean] = Some(false))
    : KrakenIO[KrakenResponse[Map[String, Trade]]] =
    O.queryTrade(transactionId, trades)

  def getOpenPositions(transactionId: Vector[String],
                       doCalcs: Option[Boolean] = Some(false))
    : KrakenIO[KrakenResponse[Map[String, OpenPosition]]] =
    O.getOpenPositions(transactionId, doCalcs)

  def getLedgersInfo(
      aClass: Option[String] = None,
      asset: Option[Vector[String]] = Some(Vector("all")),
      ledgerType: Option[LedgerType] = Some(LedgerType.all),
      start: Option[Long] = None,
      end: Option[Long] = None,
      offset: Option[Int] = None): KrakenIO[KrakenResponse[LedgerInfo]] =
    O.getLedgersInfo(aClass, asset, ledgerType, start, end, offset)

  def queryLedgers(ledgerIds: Vector[String])
    : KrakenIO[KrakenResponse[Map[String, Ledger]]] =
    O.queryLedgers(ledgerIds)

  def getTradeVolume(pair: Option[Vector[String]] = None,
                     feeInfo: Option[Boolean] = Some(true))
    : KrakenIO[KrakenResponse[TradeVolume]] =
    O.getTradeVolume(pair, feeInfo)

}
