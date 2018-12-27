package eta.cassiopeia.kraken.free.interpreters

import cats.data.Kleisli
import cats.~>
import eta.cassiopeia.kraken.KrakenApiUrls
import eta.cassiopeia.kraken.api.{PrivateApi, PublicApi}
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
        Kleisli[Future, Map[String, String], A] { credentials =>
          fa match {
            case GetServerTime => publicApi.getServerTime()

            case GetAssetInfo(info, aclass, asset) =>
              publicApi.getAssetInfo(info, aclass, asset)

            case GetAssetPairs(pair) =>
              publicApi.getAssetPairs(pair)

            case GetTickerInformation(pair) =>
              publicApi.getTickerInformation(pair)

            case GetOHLCdata(currency,
                             respectToCurrency,
                             interval,
                             timeStamp) =>
              publicApi.getOHLCdata(currency,
                                    respectToCurrency,
                                    interval,
                                    timeStamp)

            case GetOrderBook(currency, respectToCurrency, count) =>
              publicApi.getOrderBook(currency, respectToCurrency, count)

            case GetRecentTrades(currency, respectToCurrency, timeStamp) =>
              publicApi.getRecentTrades(currency, respectToCurrency, timeStamp)

            case GetRecentSpreadData(currency, respectToCurrency, timeStamp) =>
              publicApi.getRecentSpreadData(currency,
                                            respectToCurrency,
                                            timeStamp)
          }
        }
    }

  private def privateOpsInterpreter(implicit apiUrls: KrakenApiUrls,
                                    ec: ExecutionContext): PrivateOp ~> K =
    new (PrivateOp ~> K) {
      val privateApi = new PrivateApi()

      override def apply[A](fa: PrivateOp[A]): K[A] =
        Kleisli[Future, Map[String, String], A] { credentials =>
          fa match {
            case GetAccountBalance => privateApi.getAccountBalance(credentials)

            case GetTradeBalance(aClass, asset) =>
              privateApi.getTradeBalance(credentials, aClass, asset)

            case GetOpenOrders(trades, userref) =>
              privateApi.getOpenOrders(credentials, trades, userref)

            case GetClosedOrder(trades, userref, start, end, ofs, closeTime) =>
              privateApi.getClosedOrders(credentials,
                                         trades,
                                         userref,
                                         start,
                                         end,
                                         ofs,
                                         closeTime)

            case QueryOrders(txid, trades, userref) =>
              privateApi.queryOrders(credentials, txid, trades, userref)

            case GetTradesHistory(positionType, trades, start, end, ofs) =>
              privateApi.getTradesHistory(credentials,
                                          positionType,
                                          trades,
                                          start,
                                          end,
                                          ofs)

            case QueryTrades(txid, trades) =>
              privateApi.queryTrades(credentials, txid, trades)

            case GetOpenPositions(transactionId, doCalcs) =>
              privateApi.getOpenPositions(credentials, transactionId, doCalcs)

            case GetLedgersInfo(aClass,
                                asset,
                                ledgerType,
                                start,
                                end,
                                offset) =>
              privateApi.getLedgersInfo(credentials,
                                        aClass,
                                        asset,
                                        ledgerType,
                                        start,
                                        end,
                                        offset)

            case QueryLedgers(ledgerIds) =>
              privateApi.queryLedgers(credentials, ledgerIds)

            case GetTradeVolume(pair, feeInfo) =>
              privateApi.getTradeVolume(credentials, pair, feeInfo)
          }
        }
    }
}
