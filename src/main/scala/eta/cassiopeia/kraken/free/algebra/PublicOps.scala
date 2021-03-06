package eta.cassiopeia.kraken.free.algebra

import cats.InjectK
import cats.free.Free
import eta.cassiopeia.kraken.KrakenResponses.KrakenResponse
import eta.cassiopeia.kraken.free.domain._

import scala.language.higherKinds

sealed trait PublicOp[A]

case object GetServerTime extends PublicOp[KrakenResponse[ServerTime]]

case class GetAssetInfo(info: Option[String],
                        aclass: Option[String],
                        asset: Option[List[String]])
    extends PublicOp[KrakenResponse[Map[String, Asset]]]

case class GetAssetPairs(pair: Option[List[(String, String)]])
    extends PublicOp[KrakenResponse[Map[String, AssetPair]]]

case class GetTickerInformation(pair: List[(String, String)])
    extends PublicOp[KrakenResponse[Map[String, Ticker]]]

case class GetOHLCdata(currency: String,
                       respectToCurrency: String,
                       interval: Option[Int] = None,
                       timeStamp: Option[Long] = None)
    extends PublicOp[KrakenResponse[DataWithTime[OHLC]]]

case class GetOrderBook(currency: String,
                        respectToCurrency: String,
                        count: Option[Int])
    extends PublicOp[KrakenResponse[Map[String, AsksAndBids]]]

case class GetRecentTrades(currency: String,
                           respectToCurrency: String,
                           timeStamp: Option[Long])
    extends PublicOp[KrakenResponse[DataWithTime[RecentTrade]]]

case class GetRecentSpreadData(currency: String,
                               respectToCurrency: String,
                               timeStamp: Option[Long])
    extends PublicOp[KrakenResponse[DataWithTime[RecentSpread]]]

class PublicOps[F[_]](implicit I: InjectK[PublicOp, F]) {
  def getServerTime: Free[F, KrakenResponse[ServerTime]] =
    Free.inject[PublicOp, F](GetServerTime)

  def getAssetInfo(info: Option[String],
                   aclass: Option[String],
                   asset: Option[List[String]])
    : Free[F, KrakenResponse[Map[String, Asset]]] =
    Free.inject[PublicOp, F](GetAssetInfo(info, aclass, asset))

  def getAssetPairs(pair: Option[List[(String, String)]])
    : Free[F, KrakenResponse[Map[String, AssetPair]]] =
    Free.inject[PublicOp, F](GetAssetPairs(pair))

  def getTickerInformation(pair: List[(String, String)])
    : Free[F, KrakenResponse[Map[String, Ticker]]] =
    Free.inject[PublicOp, F](GetTickerInformation(pair))

  def getOHLCdata(
      currency: String,
      respectToCurrency: String,
      interval: Option[Int],
      timeStamp: Option[Long]): Free[F, KrakenResponse[DataWithTime[OHLC]]] =
    Free.inject[PublicOp, F](
      GetOHLCdata(currency, respectToCurrency, interval, timeStamp))

  def getOrderBook(
      currency: String,
      respectToCurrency: String,
      count: Option[Int]): Free[F, KrakenResponse[Map[String, AsksAndBids]]] =
    Free.inject[PublicOp, F](GetOrderBook(currency, respectToCurrency, count))

  def getRecentTrades(currency: String,
                      respectToCurrency: String,
                      timeStamp: Option[Long])
    : Free[F, KrakenResponse[DataWithTime[RecentTrade]]] =
    Free.inject[PublicOp, F](
      GetRecentTrades(currency, respectToCurrency, timeStamp))

  def getRecentSpreadData(currency: String,
                          respectToCurrency: String,
                          timeStamp: Option[Long])
    : Free[F, KrakenResponse[DataWithTime[RecentSpread]]] =
    Free.inject[PublicOp, F](
      GetRecentSpreadData(currency, respectToCurrency, timeStamp))
}

object PublicOps {
  implicit def instance[F[_]](implicit I: InjectK[PublicOp, F]): PublicOps[F] =
    new PublicOps[F]
}
