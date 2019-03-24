package pesto

package object implicits {
  implicit class HrefOps(val href: String) {
    def toOption: Option[String] = if (href.isEmpty) None else Some(href)
  }
}
