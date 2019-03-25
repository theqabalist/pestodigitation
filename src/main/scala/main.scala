import cats.effect._
import cats.data._
import cats.implicits._
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
  import pesto.http.HttpMonad.forKleisli

  def verify(recipe: Recipe): RIO[Vector[Recipe]] =
    recipe.href
      .flatMap(Uri.fromString(_).toOption)
      .map(HttpMonad[RIO].fetchRaw(_).as(recipe.pure[Vector]).handleError(_ => Vector.empty))
      .getOrElse(Vector.empty.pure[RIO])

  def run(args: List[String]) = {
    val fetch = for {
      results <- query[RIO](args(0), args(1))
      verified <- results.parFlatTraverse(verify)
    } yield verified

    AsyncHttpClient
      .resource[IO]()
      .use(fetch.run)
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
