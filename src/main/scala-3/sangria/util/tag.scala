package sangria.util

object tag {
  def apply[U] = new Tagger[U]

  type Tag[U]
  opaque type Tagged[U] = Tag[U]
  type @@[+T, U] = (T & Tagged[U]) | Null

  class Tagger[U] {
    def apply[T](t: T): T @@ U = t.asInstanceOf[T @@ U]
  }
}
