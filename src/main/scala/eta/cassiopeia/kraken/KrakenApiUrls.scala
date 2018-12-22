package eta.cassiopeia.kraken

case class KrakenApiUrls(
    baseUrl: String
)

object KrakenApiUrls {

  implicit val defaultUrls: KrakenApiUrls = KrakenApiUrls(
    "https://api.kraken.com"
  )
}
