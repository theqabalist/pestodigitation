import io.circe.{Decoder}
import cats.effect._
import org.http4s.client.blaze._
import org.http4s.circe._
import scala.concurrent.ExecutionContext.global
import org.http4s.Uri
import org.http4s.client.middleware.FollowRedirect

package pesto {
  package object http {
    implicit val cs: ContextShift[IO] = IO.contextShift(global)
    implicit val timer: Timer[IO] = IO.timer(global)

    val client = BlazeClientBuilder[IO](global).resource

    def fetch[A: Decoder](uri: Uri): IO[A] = client.use(c => FollowRedirect(10)(c).expect(uri)(jsonOf[IO, A]))
    def fetchRaw(uri: Uri): IO[String] = client.use(c => FollowRedirect(10)(c).expect[String](uri))
  }
}
