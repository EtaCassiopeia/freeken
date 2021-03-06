package eta.cassiopeia.kraken

import cats.data.Kleisli
import eta.cassiopeia.kraken.KrakenResponses.{KrakenIO, KrakenResponse}
import eta.cassiopeia.kraken.free.interpreters.Interpreters

import cats.implicits._

import scala.concurrent.{ExecutionContext, Future}

class Kraken() {
  val publicApi = new KrakenPublicAPI()
  val privateApi = new KrakenPrivateAPI()
}

object Kraken {
  def apply(): Kraken =
    new Kraken()

  implicit class KrakenIOSyntaxEither[A](kio: KrakenIO[KrakenResponse[A]]) {

    def execK(implicit ec: ExecutionContext)
      : Kleisli[Future, Map[String, String], KrakenResponse[A]] =
      kio foldMap Interpreters.futureInterpreter

    def exec(credentials: Map[String, String] = Map())(
        implicit ec: ExecutionContext): Future[KrakenResponse[A]] =
      execK.run(credentials)
  }
}
