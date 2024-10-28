package sangria.marshalling

import org.scalatest.wordspec.AnyWordSpec

class FromInputSpec extends AnyWordSpec {

  "FromInput" should {
    "provide default FromInput for Map[String, Any]" in {
      val fromInput = implicitly[FromInput[Map[String, Any]]]

      assert(fromInput != null)
    }

    "provide default FromInput for Seq" in {
      import FromInputSpec.FromInputStringInstance.stringFromInput

      val fromInput = implicitly[FromInput[Seq[String]]]

      assert(fromInput != null)
    }

    "provide default FromInput for Iterable" in {
      import FromInputSpec.FromInputStringInstance.stringFromInput

      val fromInput = implicitly[FromInput[Iterable[String]]]

      assert(fromInput != null)
    }

    "provide default FromInput for Option" in {
      import FromInputSpec.FromInputStringInstance.stringFromInput

      val fromInput = implicitly[FromInput[Option[String]]]

      assert(fromInput != null)
    }

    "provide default FromInput for Option of Seq" in {
      import FromInputSpec.FromInputStringInstance.stringFromInput

      val fromInput = implicitly[FromInput[Option[Seq[String]]]]

      assert(fromInput != null)
    }

    "resolve covariant Seq" in {
      import FromInputSpec.InputType._
      import FromInputSpec.FromInputStringInstance.stringFromInput

      val fromInput = instance(OptionInputType(SeqInputType(StringInputType)))

      assert(fromInput != null)
    }
  }
}

object FromInputSpec {
  object FromInputStringInstance {
    implicit val stringFromInput: FromInput[String] = new FromInput[String] {
      override val marshaller: ResultMarshaller = CoercedScalaResultMarshaller.default
      override def fromResult(node: marshaller.Node): String = "hello"
    }
  }

  trait InputType[+T]
  object InputType {
    case class OptionInputType[T](t: InputType[T]) extends InputType[Option[T]]
    case class SeqInputType[T](t: InputType[T]) extends InputType[Seq[T]]
    case object StringInputType extends InputType[String]

    def instance[T](t: InputType[T])(implicit fromInput: FromInput[T]): FromInput[T] = fromInput
  }
}
