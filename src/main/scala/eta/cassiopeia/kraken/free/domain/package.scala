package eta.cassiopeia.kraken.free

package object domain {
  case class ServerTime(unixTime: Long, rfc1123: String)
}
