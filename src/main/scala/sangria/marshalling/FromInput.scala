package sangria.marshalling

import scala.annotation.implicitNotFound
import scala.language.higherKinds

@implicitNotFound(
  "Type ${Val} cannot be used as an input. Please consider defining an implicit instance of `FromInput` for it.")
trait FromInput[Val] {
  val marshaller: ResultMarshaller
  def fromResult(node: marshaller.Node): Val
}

object FromInput {
  private object ScalarFromInput extends FromInput[Any] {
    val marshaller: CoercedScalaResultMarshaller = CoercedScalaResultMarshaller.default
    def fromResult(node: marshaller.Node): marshaller.Node = node
  }

  class IterableFromInput[T, I[_] <: Iterable[_]](delegate: FromInput[T]) extends FromInput[I[T]] {
    val marshaller: ResultMarshaller = delegate.marshaller

    def fromResult(node: marshaller.Node): I[T] =
      node
        .asInstanceOf[I[Any]]
        .map { (e: Any) =>
          e match {
            case optElem: Option[_] =>
              optElem.map(elem => delegate.fromResult(elem.asInstanceOf[delegate.marshaller.Node]))
            case elem =>
              delegate.fromResult(elem.asInstanceOf[delegate.marshaller.Node])
          }
        }
        .asInstanceOf[I[T]]
  }

  import sangria.util.tag._

  implicit def coercedScalaInput[T]: FromInput[T @@ CoercedScalaResult] =
    ScalarFromInput.asInstanceOf[FromInput[T @@ CoercedScalaResult]]
  implicit def defaultInput[T]: FromInput[Map[String, Any]] =
    ScalarFromInput.asInstanceOf[FromInput[Map[String, Any]]]
  implicit def inputObjectResultInput[T](implicit
      ev: FromInput[T]): FromInput[T @@ InputObjectResult] =
    ev.asInstanceOf[FromInput[T @@ InputObjectResult]]

  implicit def optionInput[T](implicit ev: FromInput[T]): FromInput[Option[T]] =
    ev.asInstanceOf[FromInput[Option[T]]]
  implicit def seqInput[T](implicit ev: FromInput[T]): IterableFromInput[T, Seq] =
    new IterableFromInput[T, Seq](ev)
  implicit def iterableInput[T](implicit ev: FromInput[T]): IterableFromInput[T, Iterable] =
    new IterableFromInput[T, Iterable](ev)

  trait CoercedScalaResult
  trait InputObjectResult
}
