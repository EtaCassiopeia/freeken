package eta.cassiopeia.kraken.api

import eta.cassiopeia.kraken.KrakenResponses._
import io.circe.Decoder
import io.circe.parser.decode
import scalaj.http.HttpResponse
import cats.implicits._
import eta.cassiopeia.kraken.free.domain.Response

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Either, Left, Right}

trait HttpJSupport {

  implicit class EitherOps[A, B](either: Either[A, B]) {
    def filterBimap[A1 >: A, C](p: B => Boolean,
                                fb: B => C,
                                zero: B => A1): Either[A1, C] =
      either match {
        case Right(b) if p(b) => Right(fb(b))
        case Right(b)         => Left(zero(b))
        case Left(l)          => Left(l)
      }
  }

  def toEntity[A](response: HttpResponse[String],
                  mapResponse: HttpResponse[String] => KrakenResponse[A])(
      implicit ec: ExecutionContext): Future[KrakenResponse[A]] =
    Future(response match {
      case r if r.isSuccess =>
        mapResponse(r)
      case r =>
        Either.left(
          UnsuccessfulHttpRequest(
            s"Failed invoking with status : ${r.code} body : \n ${r.body}",
            r.code
          )
        )
    })

  def decodeEntity[A: Decoder](response: HttpResponse[String])(
      implicit D: Decoder[Response[A]]): KrakenResponse[A] = {
    println(response.body)
    decode[Response[A]](response.body)
      .bimap(
        e => JsonParsingException(e.getMessage, response.body),
        r => r
      )
      .filterBimap(
        _.result.isDefined,
        _.result.get,
        e => JsonParsingException(e.error.mkString(","), response.body))
  }

  implicit class ParameterOps(params: List[String]) {
    def mkParams(start: String = "?", sep: String = "&"): String = {
      addString(new StringBuilder(), start, sep).toString
    }

    private def addString(b: StringBuilder,
                          start: String,
                          sep: String): StringBuilder = {
      var first = true

      for (x <- params) {
        if (first) {
          b append start
          b append x
          first = false
        } else {
          b append sep
          b append x
        }
      }
      b
    }
  }
}
