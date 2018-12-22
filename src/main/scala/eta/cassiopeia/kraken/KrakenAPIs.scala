package eta.cassiopeia.kraken

import eta.cassiopeia.kraken.KrakenResponses.{KrakenIO, KrakenResponse}
import eta.cassiopeia.kraken.app.KrakenOp
import eta.cassiopeia.kraken.free.algebra.PublicOps
import eta.cassiopeia.kraken.free.domain.ServerTime

class KrakenPublicAPI()(implicit O: PublicOps[KrakenOp]) {
  def getServerTime(): KrakenIO[KrakenResponse[ServerTime]] = O.getServerTime
}
