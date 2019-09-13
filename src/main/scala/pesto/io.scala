package pesto

import cats.effect.IO

package object io {
  def putStrLn(output: String): IO[Unit] = IO {
    println(output);
  }
}
