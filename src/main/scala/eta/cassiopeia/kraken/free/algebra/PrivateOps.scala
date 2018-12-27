package eta.cassiopeia.kraken.free.algebra

import cats.InjectK
import cats.free.Free
import com.sun.tools.hat.internal.server.RefsByTypeQuery
import eta.cassiopeia.kraken.KrakenResponses.KrakenResponse
import eta.cassiopeia.kraken.free.domain.CloseTime.CloseTime
import eta.cassiopeia.kraken.free.domain.LedgerType.LedgerType
import eta.cassiopeia.kraken.free.domain.TradeType.TradeType
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
                          offset: Option[Int],
                          closeTime: Option[CloseTime])
    extends PrivateOp[KrakenResponse[ClosedOrder]]

case class QueryOrders(transactionId: Vector[String],
                       trades: Option[Boolean],
                       userRef: Option[String])
    extends PrivateOp[KrakenResponse[Map[String, Order]]]

case class GetTradesHistory(positionType: Option[TradeType],
                            trades: Option[Boolean],
                            start: Option[Long],
                            end: Option[Long],
                            offset: Option[Int])
    extends PrivateOp[KrakenResponse[TradeHistory]]

case class QueryTrades(transactionId: Vector[String], trades: Option[Boolean])
    extends PrivateOp[KrakenResponse[Map[String, Trade]]]

case class GetOpenPositions(transactionId: Vector[String],
                            doCalcs: Option[Boolean])
    extends PrivateOp[KrakenResponse[Map[String, OpenPosition]]]

case class GetLedgersInfo(aClass: Option[String],
                          asset: Option[Vector[String]],
                          ledgerType: Option[LedgerType],
                          start: Option[Long],
                          end: Option[Long],
                          offset: Option[Int])
    extends PrivateOp[KrakenResponse[LedgerInfo]]

case class QueryLedgers(ledgerIds: Vector[String])
    extends PrivateOp[KrakenResponse[Map[String, Ledger]]]

case class GetTradeVolume(pair: Option[Vector[String]],
                          feeInfo: Option[Boolean])
    extends PrivateOp[KrakenResponse[TradeVolume]]

class PrivateOps[F[_]](implicit I: InjectK[PrivateOp, F]) {
  def getAccountBalance: Free[F, KrakenResponse[Map[String, String]]] =
    Free.inject[PrivateOp, F](GetAccountBalance)

  def getTradeBalance(
      aClass: Option[String],
      asset: Option[String]): Free[F, KrakenResponse[TradeBalance]] =
    Free.inject[PrivateOp, F](GetTradeBalance(aClass, asset))

  def getOpenOrders(
      trades: Option[Boolean],
      userRef: Option[String]): Free[F, KrakenResponse[OpenOrder]] =
    Free.inject[PrivateOp, F](GetOpenOrders(trades, userRef))

  def getClosedOrders(
      trades: Option[Boolean],
      userRef: Option[String],
      start: Option[Long],
      end: Option[Long],
      offset: Option[Int],
      closeTime: Option[CloseTime]): Free[F, KrakenResponse[ClosedOrder]] =
    Free.inject[PrivateOp, F](
      GetClosedOrder(trades, userRef, start, end, offset, closeTime))

  def queryOrders(
      transactionId: Vector[String],
      trades: Option[Boolean],
      userRef: Option[String]): Free[F, KrakenResponse[Map[String, Order]]] =
    Free.inject[PrivateOp, F](QueryOrders(transactionId, trades, userRef))

  def getTradesHistory(
      positionType: Option[TradeType],
      trades: Option[Boolean],
      start: Option[Long],
      end: Option[Long],
      offset: Option[Int]): Free[F, KrakenResponse[TradeHistory]] =
    Free.inject[PrivateOp, F](
      GetTradesHistory(positionType, trades, start, end, offset))

  def queryTrade(
      transactionId: Vector[String],
      trades: Option[Boolean]): Free[F, KrakenResponse[Map[String, Trade]]] =
    Free.inject[PrivateOp, F](QueryTrades(transactionId, trades))

  def getOpenPositions(transactionId: Vector[String], doCalcs: Option[Boolean])
    : Free[F, KrakenResponse[Map[String, OpenPosition]]] =
    Free.inject[PrivateOp, F](GetOpenPositions(transactionId, doCalcs))

  def getLedgersInfo(aClass: Option[String],
                     asset: Option[Vector[String]],
                     ledgerType: Option[LedgerType],
                     start: Option[Long],
                     end: Option[Long],
                     offset: Option[Int]): Free[F, KrakenResponse[LedgerInfo]] =
    Free.inject[PrivateOp, F](
      GetLedgersInfo(aClass, asset, ledgerType, start, end, offset))

  def queryLedgers(
      ledgerIds: Vector[String]): Free[F, KrakenResponse[Map[String, Ledger]]] =
    Free.inject[PrivateOp, F](QueryLedgers(ledgerIds))

  def getTradeVolume(
      pair: Option[Vector[String]],
      feeInfo: Option[Boolean]): Free[F, KrakenResponse[TradeVolume]] =
    Free.inject[PrivateOp, F](GetTradeVolume(pair, feeInfo))
}

object PrivateOps {
  implicit def instance[F[_]](
      implicit I: InjectK[PrivateOp, F]): PrivateOps[F] =
    new PrivateOps[F]
}
