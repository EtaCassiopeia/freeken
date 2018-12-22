package eta.cassiopeia.kraken.free.algebra

import cats.InjectK
import cats.free.Free
import eta.cassiopeia.kraken.KrakenResponses.KrakenResponse
import eta.cassiopeia.kraken.free.domain.{Asset, ServerTime}

import scala.language.higherKinds

sealed trait PublicOp[A]

case object GetServerTime extends PublicOp[KrakenResponse[ServerTime]]

case class GetAssetInfo(info: Option[String],
                        aclass: Option[String],
                        asset: Option[List[String]])
    extends PublicOp[KrakenResponse[Map[String, Asset]]]

class PublicOps[F[_]](implicit I: InjectK[PublicOp, F]) {
  def getServerTime: Free[F, KrakenResponse[ServerTime]] =
    Free.inject[PublicOp, F](GetServerTime)

  def getAssetInfo(info: Option[String],
                   aclass: Option[String],
                   asset: Option[List[String]])
    : Free[F, KrakenResponse[Map[String, Asset]]] =
    Free.inject[PublicOp, F](GetAssetInfo(info, aclass, asset))
}

object PublicOps {
  implicit def instance[F[_]](implicit I: InjectK[PublicOp, F]): PublicOps[F] =
    new PublicOps[F]
}
