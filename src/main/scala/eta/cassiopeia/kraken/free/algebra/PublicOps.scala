package eta.cassiopeia.kraken.free.algebra

import cats.InjectK
import cats.free.Free
import eta.cassiopeia.kraken.KrakenResponses.KrakenResponse
import eta.cassiopeia.kraken.free.domain.ServerTime

import scala.language.higherKinds

sealed trait PublicOp[A]

case object GetServerTime extends PublicOp[KrakenResponse[ServerTime]]

class PublicOps[F[_]](implicit I: InjectK[PublicOp, F]) {
  def getServerTime: Free[F, KrakenResponse[ServerTime]] =
    Free.inject[PublicOp, F](GetServerTime)
}

object PublicOps {
  implicit def instance[F[_]](implicit I: InjectK[PublicOp, F]): PublicOps[F] =
    new PublicOps[F]
}
