import cats.effect._
import cats.implicits._

import pesto.io.putStrLn
import com.recipepuppy.query
import pesto.http.fetchRaw
import org.http4s.Uri

object Main extends IOApp {
  def run(args: List[String]) = {
    for {
      results <- query(args(0), args(1))
      verified <- results.parTraverse(
        result =>
          result.href
            .flatMap(Uri.fromString(_).toOption)
            .fmap(fetchRaw(_).as(List(result)).handleErrorWith(_ => IO.pure(List())))
            .getOrElse(IO.pure(List()))
      )
      recipe = verified.flatten.maxBy(_.ingredients.size)
      repr: String = Seq(
        "Top Result\n",
        "============\n",
        s"Title: ${recipe.title}\n",
        s"Total Ingredients: ${recipe.ingredients.size}\n"
      ).foldLeft("")(_ ++ _)
      _ <- putStrLn(repr)
    } yield (ExitCode.Success)
  }
}
