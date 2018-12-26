package eta.cassiopeia.kraken.free.algebra

import cats.InjectK
import cats.free.Free
import eta.cassiopeia.kraken.KrakenResponses.KrakenResponse
import eta.cassiopeia.kraken.free.domain.{OpenOrder, Order, TradeBalance}

import scala.language.higherKinds

sealed trait PrivateOp[A]

case object GetAccountBalance
    extends PrivateOp[KrakenResponse[Map[String, String]]]

case class GetTradeBalance(aClass: Option[String], asset: Option[String])
    extends PrivateOp[KrakenResponse[TradeBalance]]

case class GetOpenOrders(trades: Option[Boolean], userref: Option[String])
    extends PrivateOp[KrakenResponse[OpenOrder]]

class PrivateOps[F[_]](implicit I: InjectK[PrivateOp, F]) {
  def getAccountBalance: Free[F, KrakenResponse[Map[String, String]]] =
    Free.inject[PrivateOp, F](GetAccountBalance)

  def getTradeBalance(
      aClass: Option[String],
      asset: Option[String]): Free[F, KrakenResponse[TradeBalance]] =
    Free.inject[PrivateOp, F](GetTradeBalance(aClass, asset))

  def getOpenTrades(
      trades: Option[Boolean],
      userref: Option[String]): Free[F, KrakenResponse[OpenOrder]] =
    Free.inject[PrivateOp, F](GetOpenOrders(trades, userref))
}

object PrivateOps {
  implicit def instance[F[_]](
      implicit I: InjectK[PrivateOp, F]): PrivateOps[F] =
    new PrivateOps[F]
}
