package eta.cassiopeia.kraken

import cats.free.Free
import eta.cassiopeia.kraken.app.KrakenOp

object KrakenResponses {

  type KrakenIO[A] = Free[KrakenOp, A]

  type KrakenResponse[A] = Either[KrakenException, A]

  sealed abstract class KrakenException(msg: String,
                                        cause: Option[Throwable] = None)
      extends Throwable(msg) {
    cause foreach initCause
  }

  case class UnsuccessfulHttpRequest(
      msg: String,
      statusCode: Int
  ) extends KrakenException(msg)

  case class JsonParsingException(
      msg: String,
      json: String
  ) extends KrakenException(msg)
}
