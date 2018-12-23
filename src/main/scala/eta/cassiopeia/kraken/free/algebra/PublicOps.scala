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
}

object PublicOps {
  implicit def instance[F[_]](implicit I: InjectK[PublicOp, F]): PublicOps[F] =
    new PublicOps[F]
}
