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
      forAll(builders) { builder =>
        builder.toList should be(List(("k1", "v1"), ("k3", "v3")))
      }
    }

    "export the data as Vector" in new PreparedBuilder {
      forAll(builders) { builder =>
        builder.toVector should be(Vector(("k1", "v1"), ("k3", "v3")))
      }
    }

    "export the data as Iterator" in new PreparedBuilder {
      forAll(builders) { builder =>
        checkIterator(builder.iterator)
        checkIterator(builder.iterator)
      }
    }

    "export the data as Iterable" in new PreparedBuilder {
      forAll(builders) { builder =>
        val iterable = builder
        iterable.toVector should be(Vector(("k1", "v1"), ("k3", "v3")))
        checkIterator(iterable.iterator)
        checkIterator(iterable.iterator)
      }
    }

    "as Iterable should handle empty collections" in new PreparedBuilder {
      val builder = new ArrayMapBuilder[String](Seq("a", "b", "c"))
      val iter = builder.iterator

      iter.hasNext should be (false)
    }

    "as Iterable should throw `NoSuchElementException` (empty)" in new PreparedBuilder {
      val builder = new ArrayMapBuilder[String](Seq("a", "b", "c"))
      val iter = builder.iterator
      
      iter.hasNext should be (false)

      a [NoSuchElementException] should be thrownBy iter.next()
    }

    "as Iterable should throw `NoSuchElementException` (non-empty)" in new PreparedBuilder {
      val builder = new ArrayMapBuilder[String](Seq("a", "b", "c"))

      builder.add("b", "v")
      val iter = builder.iterator

      iter.hasNext should be (true)
      iter.next() should be ("b" -> "v")
      iter.hasNext should be (false)

      a [NoSuchElementException] should be thrownBy iter.next()
    }

    "as Iterable should handle all keys defined" in new PreparedBuilder {
      val builder = new ArrayMapBuilder[String](Seq("a", "b", "c"))

      builder.add("c", "vc")
      builder.add("a", "va")
      builder.add("b", "vb")

      val iter = builder.iterator

      iter.hasNext should be (true)
      iter.next() should be (("a", "va"))
      iter.hasNext should be (true)
      iter.next() should be (("b", "vb"))
      iter.hasNext should be (true)
      iter.next() should be (("c", "vc"))
      iter.hasNext should be (false)
    }
  }
}
