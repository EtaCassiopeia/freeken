package eta.cassiopeia.kraken.free

import io.circe.Decoder

package object domain {
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
