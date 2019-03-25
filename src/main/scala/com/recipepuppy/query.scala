package com
package recipepuppy

import cats.implicits._
import cats.effect._
import org.http4s.Uri

import pesto.http.fetch
import pesto.PestoClient

object query {
  val baseUrl = Uri.uri("http://www.recipepuppy.com/api/")

  def apply(client: PestoClient)(ingredient: String, dish: String, page: Int = 0): IO[Vector[Recipe]] = {
    val uri = baseUrl +? ("i", ingredient) +? ("q", dish) +? ("p", page)

    fetch[ResultPage](uri)
      .flatMap(
        rp =>
          if (rp.results.isEmpty) {
            IO.pure(rp.results)
          } else {
            query(client)(ingredient, dish, page + 1).fmap(more => rp.results ++ more)
          }
      )
      .handleErrorWith(_ => query(client)(ingredient, dish, page + 1))

  }
}
