package eta.cassiopeia.kraken.api

import eta.cassiopeia.kraken.KrakenApiUrls
import eta.cassiopeia.kraken.KrakenResponses.KrakenResponse
import scalaj.http.{Http, HttpRequest}

import scala.concurrent.{ExecutionContext, Future}

class PrivateApi(implicit apiUrls: KrakenApiUrls, ec: ExecutionContext)
    extends HttpJSupport {

  private def postSignedRequest(credentials: Map[String, String],
                                path: String,
                                params: Option[String] = None): HttpRequest = {
    val nonce = System.currentTimeMillis()
    val postData = "nonce=" + nonce.toString
    val signature =
      Signer.getSignature(path, nonce, postData, credentials("API-Secret"))
    val headers =
      Map("API-Key" -> credentials("API-Key"), "API-Sign" -> signature)
    Http(url = s"${apiUrls.baseUrl}$path")
      .headers(headers)
      .postForm(List("nonce" -> nonce.toString))
  }

  def getAccountBalance(credentials: Map[String, String])
    : Future[KrakenResponse[Map[String, String]]] = {
    val request = postSignedRequest(credentials, "/0/private/Balance")

    toEntity[Map[String, String]](request.asString, decodeEntity)
  }
}
