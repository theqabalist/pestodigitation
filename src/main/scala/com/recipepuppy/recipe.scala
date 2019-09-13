package com
package recipepuppy

import io.circe.{Decoder, HCursor}
import pesto.implicits._

case class Recipe(
    href: Option[String],
    ingredients: Vector[String],
    thumbnail: Option[String],
    title: String
)

object Recipe {
  implicit val decodeRecipe: Decoder[Recipe] = new Decoder[Recipe] {
    final def apply(c: HCursor): Decoder.Result[Recipe] =
      for {
        href <- c.downField("href").as[String]
        ingredients <- c.downField("ingredients").as[String]
        thumbnail <- c.downField("thumbnail").as[String]
        title <- c.downField("title").as[String]
      } yield
        new Recipe(
          href.toOption,
          ingredients.split(",").map(_.trim).toVector,
          thumbnail.toOption,
          title.trim
        )

  }
}
