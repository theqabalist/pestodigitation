import cats.effect._
import cats.implicits._

import pesto.io.putStrLn
import pesto.PestoClient
import com.recipepuppy.query
import org.http4s.Uri
import scala.concurrent.ExecutionContext.global
import org.http4s.client.blaze._

object Main extends IOApp {
  def run(args: List[String]) = {
    BlazeClientBuilder[IO](global).resource.map(PestoClient).use(client =>
    for {
      results <- query(client)(args(0), args(1))
      verified <- results.parTraverse(
        result =>
          result.href
            .flatMap(Uri.fromString(_).toOption)
            .fmap(client.fetchRaw(_).as(List(result)).handleErrorWith(_ => IO.pure(List())))
            .getOrElse(IO.pure(List()))
      )
      recipe = verified.flatten.maxBy(_.ingredients.size)
      repr = Seq(
        "Top Result\n",
        "============\n",
        s"Title: ${recipe.title}\n",
        s"Total Ingredients: ${recipe.ingredients.size}\n"
      ).foldLeft("")(_ ++ _)
      _ <- putStrLn(repr)
    } yield (ExitCode.Success)
    )
  }
}
