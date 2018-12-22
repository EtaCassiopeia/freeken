package eta.cassiopeia.kraken.api

import eta.cassiopeia.kraken.KrakenApiUrls
import eta.cassiopeia.kraken.KrakenResponses._
import eta.cassiopeia.kraken.free.domain.{Asset, AssetPair, ServerTime}
import scalaj.http.Http

import scala.concurrent.{ExecutionContext, Future}

class PublicApi(implicit apiUrls: KrakenApiUrls, ec: ExecutionContext)
    extends HttpJSupport {
  def getServerTime(
      headers: Map[String, String]): Future[KrakenResponse[ServerTime]] = {
    val request = Http(url = s"${apiUrls.baseUrl}/0/public/Time")
      .method("GET")
      .headers(headers)

    toEntity[ServerTime](request.asString, decodeEntity)
  }

  def getAssetInfo(headers: Map[String, String],
                   info: Option[String],
                   aclass: Option[String],
                   asset: Option[List[String]])
    : Future[KrakenResponse[Map[String, Asset]]] = {
    val request = Http(url = s"${apiUrls.baseUrl}/0/public/Assets")
      .method("GET")
      .headers(headers)

    toEntity[Map[String, Asset]](request.asString, decodeEntity)
  }

  def getAssetPairs(headers: Map[String, String],
                    pair: Option[List[(String, String)]])
    : Future[KrakenResponse[Map[String, AssetPair]]] = {
    val params: String = pair
      .map(l => l.map(p => p._1 + p._2).mkString("?pair=", ",", ""))
      .getOrElse("")

    val request = Http(url = s"${apiUrls.baseUrl}/0/public/AssetPairs$params")
      .method("GET")
      .headers(headers)

    toEntity[Map[String, AssetPair]](request.asString, decodeEntity)
  }
}
