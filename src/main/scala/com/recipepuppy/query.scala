package com
package recipepuppy
import cats._
import cats.implicits._
import org.http4s.Uri

object query {
  import pesto.http._
  val baseUrl = Uri.uri("http://www.recipepuppy.com/api/")

  def apply[F[_]: HttpMonad: MonadError[?[_], Throwable]](ingredient: String, dish: String, page: Int = 0): F[Vector[Recipe]] = {
    val uri = baseUrl +? ("i", ingredient) +? ("q", dish) +? ("p", page)
    HttpMonad[F].fetch[ResultPage](uri)
      .flatMap(
        rp =>
          if (rp.results.isEmpty) {
            rp.results.pure[F]
          } else {
            query[F](ingredient, dish, page + 1).fmap(more => rp.results ++ more)
          }
      )
      .handleErrorWith(_ => query[F](ingredient, dish, page + 1))
  }
}
