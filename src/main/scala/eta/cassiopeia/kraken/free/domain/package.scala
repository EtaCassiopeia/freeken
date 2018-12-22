package eta.cassiopeia.kraken.free

import io.circe.Decoder

package object domain {

  case class Response[T](error: List[String], result: Option[T])

  object Response {
    implicit def decodeResponse[A: Decoder]: Decoder[Response[A]] =
      Decoder.instance { c =>
        for {
          error <- c.downField("error").as[List[String]]
          result <- c.downField("result").as[Option[A]]
        } yield Response(error, result)
      }
  }

  case class ServerTime(unixTime: Long, rfc1123: String)

  object ServerTime {
    implicit val decodeServerTime: Decoder[ServerTime] = Decoder.instance { c =>
      for {
        unixTime <- c.downField("unixtime").as[Long]
        rfc1123 <- c.downField("rfc1123").as[String]
      } yield ServerTime(unixTime, rfc1123)
    }
  }
}
