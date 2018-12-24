package eta.cassiopeia.kraken.api

import eta.cassiopeia.kraken.KrakenApiUrls
import eta.cassiopeia.kraken.KrakenResponses.KrakenResponse
import eta.cassiopeia.kraken.free.domain.{OpenOrder, Order, TradeBalance}
import scalaj.http.{Http, HttpRequest}

import scala.concurrent.{ExecutionContext, Future}

class PrivateApi(implicit apiUrls: KrakenApiUrls, ec: ExecutionContext)
    extends HttpJSupport {

  private def postSignedRequest(
      credentials: Map[String, String],
      path: String,
      params: Map[String, String] = Map.empty): HttpRequest = {
    val nonce = System.currentTimeMillis()
    val postData = "nonce=" + nonce.toString + params
      .map(e => e._1 + "=" + e._2)
      .toList
      .mkParams(start = "&")
    val signature =
      Signer.getSignature(path, nonce, postData, credentials("API-Secret"))
    val headers =
      Map("API-Key" -> credentials("API-Key"), "API-Sign" -> signature)
    Http(url = s"${apiUrls.baseUrl}$path")
      .headers(headers)
      .postForm(List("nonce" -> nonce.toString) ++ params.toList)
  }

  def getAccountBalance(credentials: Map[String, String])
    : Future[KrakenResponse[Map[String, String]]] = {
    val request = postSignedRequest(credentials, "/0/private/Balance")

    toEntity[Map[String, String]](request.asString, decodeEntity)
  }

  def getTradeBalance(
      credentials: Map[String, String],
      aClass: Option[String],
      asset: Option[String]): Future[KrakenResponse[List[TradeBalance]]] = {

    val params =
      List(aClass.map("aclass" -> _), asset.map("asset" -> _)).flatten.toMap

    val request =
      postSignedRequest(credentials, path = "/0/private/TradeBalance", params)

    toEntity[List[TradeBalance]](request.asString, decodeEntity)
  }

  def getOpenOrders(
      credentials: Map[String, String],
      trades: Option[Boolean],
      userref: Option[String]): Future[KrakenResponse[OpenOrder]] = {

    val params =
      List(trades.map("trades" -> _.toString.toLowerCase),
           userref.map("userref" -> _)).flatten.toMap

    val request =
      postSignedRequest(credentials,
                        path = "/0/private/OpenOrders",
                        params = params)

    toEntity[OpenOrder](request.asString, decodeEntity)
  }
}
