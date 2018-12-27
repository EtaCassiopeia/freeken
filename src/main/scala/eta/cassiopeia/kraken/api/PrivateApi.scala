package eta.cassiopeia.kraken.api

import eta.cassiopeia.kraken.KrakenApiUrls
import eta.cassiopeia.kraken.KrakenResponses.KrakenResponse
import eta.cassiopeia.kraken.free.domain.CloseTime.CloseTime
import eta.cassiopeia.kraken.free.domain.LedgerType.LedgerType
import eta.cassiopeia.kraken.free.domain.TradeType.TradeType
import eta.cassiopeia.kraken.free.domain._
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
      .map { case (k, v) => s"$k=$v" }
      .toList
      .mkParams(start = "&")
    val signature =
      Signer.getSignature(path, nonce, postData, credentials("API-Secret"))
    val headers =
      Map("API-Key" -> credentials("API-Key"), "API-Sign" -> signature)
    Http(url = s"${apiUrls.baseUrl}$path")
      .headers(headers)
      .postForm(("nonce" -> nonce.toString) +: params.toList)
  }

  def getAccountBalance(credentials: Map[String, String])
    : Future[KrakenResponse[Map[String, String]]] = {
    val request = postSignedRequest(credentials, "/0/private/Balance")

    toEntity[Map[String, String]](request.asString, decodeEntity)
  }

  def getTradeBalance(
      credentials: Map[String, String],
      aClass: Option[String],
      asset: Option[String]): Future[KrakenResponse[TradeBalance]] = {

    val params =
      List(aClass.map("aclass" -> _), asset.map("asset" -> _)).flatten.toMap

    val request =
      postSignedRequest(credentials, path = "/0/private/TradeBalance", params)

    toEntity[TradeBalance](request.asString, decodeEntity)
  }

  def getOpenOrders(
      credentials: Map[String, String],
      trades: Option[Boolean],
      userRef: Option[String]): Future[KrakenResponse[OpenOrder]] = {

    val params =
      List(trades.map("trades" -> _.toString.toLowerCase),
           userRef.map("userref" -> _)).flatten.toMap

    val request =
      postSignedRequest(credentials,
                        path = "/0/private/OpenOrders",
                        params = params)

    toEntity[OpenOrder](request.asString, decodeEntity)
  }

  def getClosedOrders(
      credentials: Map[String, String],
      trades: Option[Boolean],
      userRef: Option[String],
      start: Option[Long],
      end: Option[Long],
      ofs: Option[Int],
      closeTime: Option[CloseTime]): Future[KrakenResponse[ClosedOrder]] = {

    val params =
      List(
        trades.map("trades" -> _.toString.toLowerCase),
        userRef.map("userref" -> _),
        start.map("start" -> _.toString),
        end.map("end" -> _.toString),
        ofs.map("ofs" -> _.toString),
        closeTime.map("closetime" -> _.toString)
      ).flatten.toMap

    val request =
      postSignedRequest(credentials,
                        path = "/0/private/ClosedOrders",
                        params = params)

    toEntity[ClosedOrder](request.asString, decodeEntity)
  }

  def queryOrders(
      credentials: Map[String, String],
      transactionId: Vector[String],
      trades: Option[Boolean],
      userRef: Option[String]): Future[KrakenResponse[Map[String, Order]]] = {
    val params = List(trades.map("trades" -> _.toString.toLowerCase),
                      userRef.map("userref" -> _),
                      Some("txid" -> transactionId.mkString(","))).flatten.toMap

    val request = postSignedRequest(credentials,
                                    path = "/0/private/QueryOrders",
                                    params = params)

    toEntity[Map[String, Order]](request.asString, decodeEntity)
  }

  def getTradesHistory(
      credentials: Map[String, String],
      positionType: Option[TradeType],
      trades: Option[Boolean],
      start: Option[Long],
      end: Option[Long],
      offset: Option[Int]): Future[KrakenResponse[TradeHistory]] = {
    val params = List(
      positionType.map("type" -> _.toString),
      trades.map("trades" -> _.toString.toLowerCase),
      start.map("start" -> _.toString),
      end.map("end" -> _.toString),
      offset.map("ofs" -> _.toString)
    ).flatten.toMap

    val request = postSignedRequest(credentials,
                                    path = "/0/private/TradesHistory",
                                    params = params)

    toEntity[TradeHistory](request.asString, decodeEntity)
  }

  def queryTrades(
      credentials: Map[String, String],
      transactionId: Vector[String],
      trades: Option[Boolean]): Future[KrakenResponse[Map[String, Trade]]] = {
    val params = List(trades.map("trades" -> _.toString.toLowerCase),
                      Some("txid" -> transactionId.mkString(","))).flatten.toMap

    val request = postSignedRequest(credentials,
                                    path = "/0/private/QueryTrades",
                                    params = params)

    toEntity[Map[String, Trade]](request.asString, decodeEntity)
  }

  def getOpenPositions(credentials: Map[String, String],
                       transactionId: Vector[String],
                       doCalcs: Option[Boolean])
    : Future[KrakenResponse[Map[String, OpenPosition]]] = {
    val params = List(doCalcs.map("docalcs" -> _.toString.toLowerCase),
                      Some("txid" -> transactionId.mkString(","))).flatten.toMap

    val request = postSignedRequest(credentials,
                                    path = "/0/private/OpenPositions",
                                    params = params)

    toEntity[Map[String, OpenPosition]](request.asString, decodeEntity)
  }

  def getLedgersInfo(
      credentials: Map[String, String],
      aClass: Option[String],
      asset: Option[Vector[String]],
      ledgerType: Option[LedgerType],
      start: Option[Long],
      end: Option[Long],
      offset: Option[Int]): Future[KrakenResponse[LedgerInfo]] = {
    val params = List(
      aClass.map("aclass" -> _),
      asset.map("asset" -> _.mkString(",")),
      ledgerType.map("type" -> _.toString),
      start.map("start" -> _.toString),
      end.map("end" -> _.toString),
      offset.map("ofs" -> _.toString)
    ).flatten.toMap

    val request = postSignedRequest(credentials,
                                    path = "/0/private/Ledgers",
                                    params = params)

    toEntity[LedgerInfo](request.asString, decodeEntity)
  }

  def queryLedgers(credentials: Map[String, String], ledgerIds: Vector[String])
    : Future[KrakenResponse[Map[String, Ledger]]] = {
    val params = Map("id" -> ledgerIds.mkString(","))

    val request = postSignedRequest(credentials,
                                    path = "/0/private/QueryLedgers",
                                    params = params)

    println(request.toString)

    toEntity[Map[String, Ledger]](request.asString, decodeEntity)
  }

  def getTradeVolume(
      credentials: Map[String, String],
      pair: Option[Vector[String]],
      feeInfo: Option[Boolean]): Future[KrakenResponse[TradeVolume]] = {
    val params = List(pair.map("pair" -> _.mkString(",")),
                      feeInfo.map("fee_info" -> _.toString)).flatten.toMap

    val request = postSignedRequest(credentials,
                                    path = "/0/private/TradeVolume",
                                    params = params)

    toEntity[TradeVolume](request.asString, decodeEntity)
  }
}
