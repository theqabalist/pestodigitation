import io.circe.{Decoder}
import cats.data._
import cats.effect._
import org.http4s.client._
import org.http4s.circe._
import org.http4s.Uri
import org.http4s.client.middleware.FollowRedirect

package pesto {
  package http {
    trait HttpMonad[F[_]] {
      def fetch[A: Decoder](uri: Uri): F[A]
      def fetchRaw(uri: Uri): F[String]
    }
  
    object HttpMonad {
      def apply[F[_]](implicit HttpMonad: HttpMonad[F]): HttpMonad[F] = HttpMonad
      
      implicit def forKleisli[F[_]: Sync]: HttpMonad[Kleisli[F, Client[F], ?]] = new HttpMonad[Kleisli[F, Client[F], ?]] {
        def fetch[A: Decoder](uri: Uri): Kleisli[F, Client[F], A] = 
          Kleisli(client => FollowRedirect[F](10)(client).expect(uri)(jsonOf[F, A]))
  
        def fetchRaw(uri: Uri): Kleisli[F, Client[F], String] = 
          Kleisli(client => FollowRedirect[F](10)(client).expect[String](uri))
      }
    }
  }
}
