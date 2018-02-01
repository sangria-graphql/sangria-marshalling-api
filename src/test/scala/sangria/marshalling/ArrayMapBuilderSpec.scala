package sangria.marshalling

import org.scalatest.{Assertion, Inspectors, Matchers, WordSpec}

class ArrayMapBuilderSpec extends WordSpec with Matchers with Inspectors {

  trait PreparedBuilder {
    val builder1 = new ArrayMapBuilder[String](Seq("k1", "k2", "k3", "k4"))
    builder1.add("k1", "v1")
    builder1.add("k3", "v3")

    val builder2 = new ArrayMapBuilder[String](Seq("k1", "k3"))
    builder2.add("k3", "v3")
    builder2.add("k1", "v1")

    val builder3 = new ArrayMapBuilder[String](Seq("k0", "k1", "k2", "k3"))
    builder3.add("k1", "v1")
    builder3.add("k3", "v3")

    val builders = builder1 :: builder2 :: builder3 :: Nil
  }

  private def checkIterator(iterator: Iterator[(String, String)]): Assertion = {
    iterator.hasNext should be (true)
    iterator.next() should be (("k1", "v1"))
    iterator.hasNext should be (true)
    iterator.next() should be (("k3", "v3"))
    iterator.hasNext should be (false)
  }

  "ArrayMapBuilder" should {
    "export the data as List" in new PreparedBuilder {
      forAll(builders) { builder ⇒
        builder.toList should be(List(("k1", "v1"), ("k3", "v3")))
      }
    }
    "export the data as Vector" in new PreparedBuilder {
      forAll(builders) { builder ⇒
        builder.toVector should be(Vector(("k1", "v1"), ("k3", "v3")))
      }
    }
    "export the data as Iterator" in new PreparedBuilder {
      forAll(builders) { builder ⇒
        checkIterator(builder.toIterator)
        checkIterator(builder.toIterator)
      }
    }
    "export the data as Iterable" in new PreparedBuilder {
      forAll(builders) { builder ⇒
        val iterable = builder
        iterable.toVector should be(Vector(("k1", "v1"), ("k3", "v3")))
        checkIterator(iterable.iterator)
        checkIterator(iterable.iterator)
      }
    }
  }

}
