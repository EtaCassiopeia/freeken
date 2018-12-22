package eta.cassiopeia.kraken.api

import eta.cassiopeia.kraken.KrakenApiUrls
import eta.cassiopeia.kraken.KrakenResponses._
import eta.cassiopeia.kraken.free.domain.ServerTime
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
}
