import cats._
import cats.effect._
import cats.data._
import cats.implicits._
import cats.mtl._
import pesto.io.putStrLn
import com.recipepuppy.query
import org.http4s.Uri
import org.http4s.client.asynchttpclient._
import org.http4s.client._
import pesto.http.HttpMonad
import com.recipepuppy._

object Main extends IOApp {
  type RIO[A] = Kleisli[IO, Client[IO], A]
  // This is probably not the best way to provide this...
  import pesto.http.HttpMonad._

  def verify[F[_]: HttpMonad: MonadError[?[_], Throwable]](recipe: Recipe): F[Vector[Recipe]] =
    recipe.href
      .flatMap(Uri.fromString(_).toOption)
      .map(HttpMonad[F].fetchRaw(_).as(recipe.pure[Vector]).handleError(_ => Vector.empty))
      .getOrElse(Vector.empty.pure[F])

  def askConst[F[_]: Applicative, C](c: C): ApplicativeAsk[F, C] = new ApplicativeAsk[F, C] {
    val applicative = Applicative[F]
    val ask = c.pure[F]
    def reader[A](f: (C) => A): F[A] = ask.map(f)
  }

  def run(args: List[String]) = {
    AsyncHttpClient
      .resource[IO]()
      .map(askConst[IO, Client[IO]])
      .use(implicit env => {
        for {
          results <- query[IO](args(0), args(1))
          verified <- results.parFlatTraverse(verify[IO])
        } yield verified
      })
      .flatMap(verified => {
        val recipe = verified.maxBy(_.ingredients.size)
        val repr = Vector(
          "Top Result\n",
          "============\n",
          s"Title: ${recipe.title}\n",
          s"Total Ingredients: ${recipe.ingredients.size}\n"
        ).foldLeft("")(_ ++ _)
        putStrLn(repr)
      })
      .as(ExitCode.Success)
  }
}
