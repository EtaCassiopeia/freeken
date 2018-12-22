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

  case class Asset(aClass: String,
                   altName: String,
                   decimals: Int,
                   displayDecimals: Int)

  case object Asset {
    implicit val decodeAsset: Decoder[Asset] = Decoder.instance { c =>
      for {
        aClass <- c.downField("aclass").as[String]
        altName <- c.downField("altname").as[String]
        decimals <- c.downField("decimals").as[Int]
        displayDecimals <- c.downField("display_decimals").as[Int]
      } yield Asset(aClass, altName, decimals, displayDecimals)
    }
  }

  case class AssetPair(altName: String,
                       aClassBase: String,
                       base: String,
                       aClassQuote: String,
                       quote: String,
                       lot: String,
                       pairDecimals: Int,
                       lotDecimals: Int,
                       lotMultiplier: Int,
                       leverageBuy: List[Int],
                       leverageSell: List[Int],
                       fees: List[List[Double]],
                       feesMaker: Option[List[List[Double]]],
                       feeVolumeCurrency: String,
                       marginCall: Int,
                       marginStop: Int)

  object AssetPair {
    implicit val decodeAssetPair: Decoder[AssetPair] = Decoder.instance { c =>
      for {
        altName <- c.downField("altname").as[String]
        aClassBase <- c.downField("aclass_base").as[String]
        base <- c.downField("base").as[String]
        aClassQuote <- c.downField("aclass_quote").as[String]
        quote <- c.downField("quote").as[String]
        lot <- c.downField("lot").as[String]
        pairDecimals <- c.downField("pair_decimals").as[Int]
        lotDecimals <- c.downField("lot_decimals").as[Int]
        lotMultiplier <- c.downField("lot_multiplier").as[Int]
        leverageBuy <- c.downField("leverage_buy").as[List[Int]]
        leverageSell <- c.downField("leverage_sell").as[List[Int]]
        fees <- c.downField("fees").as[List[List[Double]]]
        feesMaker <- c.downField("fees_maker").as[Option[List[List[Double]]]]
        feeVolumeCurrency <- c.downField("fee_volume_currency").as[String]
        marginCall <- c.downField("margin_call").as[Int]
        marginStop <- c.downField("margin_stop").as[Int]
      } yield
        AssetPair(
          altName,
          aClassBase,
          base,
          aClassQuote,
          quote,
          lot,
          pairDecimals,
          lotDecimals,
          lotMultiplier,
          leverageBuy,
          leverageSell,
          fees,
          feesMaker,
          feeVolumeCurrency,
          marginCall,
          marginStop
        )
    }
  }
}
