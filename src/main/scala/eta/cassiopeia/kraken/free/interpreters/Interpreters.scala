package eta.cassiopeia.kraken.free.interpreters

import cats.data.Kleisli
import cats.~>
import eta.cassiopeia.kraken.KrakenApiUrls
import eta.cassiopeia.kraken.api.PublicApi
import eta.cassiopeia.kraken.app.KrakenOp
import eta.cassiopeia.kraken.free.algebra._

import scala.concurrent.{ExecutionContext, Future}

object Interpreters {

  type K[A] = Kleisli[Future, Map[String, String], A]

  def futureInterpreter(implicit apiUrls: KrakenApiUrls,
                        ec: ExecutionContext): KrakenOp ~> K =
    publicOpsInterpreter or privateOpsInterpreter

  private def publicOpsInterpreter(implicit apiUrls: KrakenApiUrls,
                                   ec: ExecutionContext): PublicOp ~> K =
    new (PublicOp ~> K) {
      val publicApi = new PublicApi()

      override def apply[A](fa: PublicOp[A]): K[A] =
        Kleisli[Future, Map[String, String], A] { headers =>
          fa match {
            case GetServerTime => publicApi.getServerTime(headers)
            case GetAssetInfo(info, aclass, asset) =>
              publicApi.getAssetInfo(headers, info, aclass, asset)
            case GetAssetPairs(pair) => publicApi.getAssetPairs(headers, pair)
            case GetTickerInformation(pair) =>
              publicApi.getTickerInformation(headers, pair)
            case GetOHLCdata(currency,
                             respectToCurrency,
                             interval,
                             timeStamp) =>
              publicApi.getOHLCdata(headers,
                                    currency,
                                    respectToCurrency,
                                    interval,
                                    timeStamp)
          }
        }
    }

  private def privateOpsInterpreter(implicit apiUrls: KrakenApiUrls,
                                    ec: ExecutionContext): PrivateOp ~> K =
    new (PrivateOp ~> K) {
      override def apply[A](fa: PrivateOp[A]): K[A] = ???
    }
}
