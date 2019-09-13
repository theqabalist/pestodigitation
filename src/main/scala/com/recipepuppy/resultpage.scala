package com
package recipepuppy

import io.circe.{Decoder, HCursor}
import pesto.implicits._

case class ResultPage(
    href: Option[String],
    results: Vector[Recipe],
    title: String,
    version: String
)

object ResultPage {
  implicit val decodeRecipe: Decoder[ResultPage] = new Decoder[ResultPage] {
    final def apply(c: HCursor): Decoder.Result[ResultPage] =
      for {
        href <- c.downField("href").as[String]
        results <- c.downField("results").as[Vector[Recipe]]
        version <- c.downField("version").as[Float]
        title <- c.downField("title").as[String]
      } yield
        new ResultPage(
          href.toOption,
          results,
          version.toString,
          title.trim
        )

  }
}
