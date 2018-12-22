package eta.cassiopeia.kraken

import cats.data.EitherK
import eta.cassiopeia.kraken.free.algebra.{PrivateOp, PublicOp}

object app {
  type KrakenOp[A] = EitherK[PublicOp, PrivateOp, A]
}
