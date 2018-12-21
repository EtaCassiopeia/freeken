package eta.cassiopeia.kraken

object KrakenResponses {

  type KrakenResponse[A] = Either[KrakenException, A]

  sealed abstract class KrakenException(msg: String,
                                        cause: Option[Throwable] = None)
      extends Throwable(msg) {
    cause foreach initCause
  }
}
