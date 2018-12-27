package eta.cassiopeia.kraken.free.algebra

import cats.InjectK
import cats.free.Free
import com.sun.tools.hat.internal.server.RefsByTypeQuery
import eta.cassiopeia.kraken.KrakenResponses.KrakenResponse
import eta.cassiopeia.kraken.free.domain.CloseTime.CloseTime
import eta.cassiopeia.kraken.free.domain.PositionType.PositionType
import eta.cassiopeia.kraken.free.domain._

import scala.language.higherKinds

sealed trait PrivateOp[A]

case object GetAccountBalance
    extends PrivateOp[KrakenResponse[Map[String, String]]]

case class GetTradeBalance(aClass: Option[String], asset: Option[String])
    extends PrivateOp[KrakenResponse[TradeBalance]]

case class GetOpenOrders(trades: Option[Boolean], userref: Option[String])
    extends PrivateOp[KrakenResponse[OpenOrder]]

case class GetClosedOrder(trades: Option[Boolean],
                          userref: Option[String],
                          start: Option[Long],
                          end: Option[Long],
                          ofs: Option[Int],
                          closeTime: Option[CloseTime])
    extends PrivateOp[KrakenResponse[ClosedOrder]]

case class QueryOrders(txid: Vector[String],
                       trades: Option[Boolean],
                       userref: Option[String])
    extends PrivateOp[KrakenResponse[Map[String, Order]]]

case class GetTradesHistory(positionType: Option[PositionType],
                            trades: Option[Boolean],
                            start: Option[Long],
                            end: Option[Long],
                            ofs: Option[Int])
    extends PrivateOp[KrakenResponse[TradeHistory]]

case class QueryTrades(txid: Vector[String], trades: Option[Boolean])
    extends PrivateOp[KrakenResponse[Map[String, Trade]]]

class PrivateOps[F[_]](implicit I: InjectK[PrivateOp, F]) {
  def getAccountBalance: Free[F, KrakenResponse[Map[String, String]]] =
    Free.inject[PrivateOp, F](GetAccountBalance)

  def getTradeBalance(
      aClass: Option[String],
      asset: Option[String]): Free[F, KrakenResponse[TradeBalance]] =
    Free.inject[PrivateOp, F](GetTradeBalance(aClass, asset))

  def getOpenOrders(
      trades: Option[Boolean],
      userref: Option[String]): Free[F, KrakenResponse[OpenOrder]] =
    Free.inject[PrivateOp, F](GetOpenOrders(trades, userref))

  def getClosedOrders(
      trades: Option[Boolean],
      userref: Option[String],
      start: Option[Long],
      end: Option[Long],
      ofs: Option[Int],
      closeTime: Option[CloseTime]): Free[F, KrakenResponse[ClosedOrder]] =
    Free.inject[PrivateOp, F](
      GetClosedOrder(trades, userref, start, end, ofs, closeTime))

  def queryOrders(
      txid: Vector[String],
      trades: Option[Boolean],
      userref: Option[String]): Free[F, KrakenResponse[Map[String, Order]]] =
    Free.inject[PrivateOp, F](QueryOrders(txid, trades, userref))

  def getTradesHistory(
      positionType: Option[PositionType],
      trades: Option[Boolean],
      start: Option[Long],
      end: Option[Long],
      ofs: Option[Int]): Free[F, KrakenResponse[TradeHistory]] =
    Free.inject[PrivateOp, F](
      GetTradesHistory(positionType, trades, start, end, ofs))

  def queryTrade(
      txid: Vector[String],
      trades: Option[Boolean]): Free[F, KrakenResponse[Map[String, Trade]]] =
    Free.inject[PrivateOp, F](QueryTrades(txid, trades))
}

object PrivateOps {
  implicit def instance[F[_]](
      implicit I: InjectK[PrivateOp, F]): PrivateOps[F] =
    new PrivateOps[F]
}
