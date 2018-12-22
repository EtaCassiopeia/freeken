package eta.cassiopeia.kraken

import eta.cassiopeia.kraken.KrakenResponses.{KrakenIO, KrakenResponse}
import eta.cassiopeia.kraken.app.KrakenOp
import eta.cassiopeia.kraken.free.algebra.PublicOps
import eta.cassiopeia.kraken.free.domain.{Asset, AssetPair, ServerTime}

class KrakenPublicAPI()(implicit O: PublicOps[KrakenOp]) {
  def getServerTime(): KrakenIO[KrakenResponse[ServerTime]] = O.getServerTime

  def getAssetInfo(info: Option[String] = None,
                   aclass: Option[String] = None,
                   asset: Option[List[String]] = None)
    : KrakenIO[KrakenResponse[Map[String, Asset]]] =
    O.getAssetInfo(info, aclass, asset)

  def getAssetPair(pair: Option[List[(String, String)]] = None)
    : KrakenIO[KrakenResponse[Map[String, AssetPair]]] = O.getAssetPairs(pair)
}