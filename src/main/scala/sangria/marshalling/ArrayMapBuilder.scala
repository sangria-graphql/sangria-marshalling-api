package sangria.marshalling

import scala.annotation.tailrec
import scala.collection.immutable.{ListMap, VectorBuilder}
import scala.collection.mutable.{Set ⇒ MutableSet}

/**
  * GraphQL `Map` builder that knows keys in advance and able to preserve an original fields sort order
  */
class ArrayMapBuilder[T](keys: Seq[String]) {
  private val elements = new Array[(String, T)](keys.size)
  private val indexLookup = keys.zipWithIndex.toMap
  private val indexesSet = MutableSet[Int]()

  def add(key: String, elem: T) = {
    val idx = indexLookup(key)

    elements(idx) = key → elem
    indexesSet += idx

    this
  }


  lazy val toList: List[(String, T)] = {
    val builder = List.newBuilder[(String, T)]

    for (i ← 0 to elements.length if indexesSet contains i) {
      builder += elements(i)
    }

    builder.result()
  }

  lazy val toMap: Map[String, T] = {
    val builder = Map.newBuilder[String, T]

    for (i ← 0 to elements.length if indexesSet contains i) {
      builder += elements(i)
    }

    builder.result()
  }

  lazy val toListMap: ListMap[String, T] = {
    val builder = ListMap.newBuilder[String, T]

    for (i ← 0 to elements.length if indexesSet contains i) {
      builder += elements(i)
    }

    builder.result()
  }

  lazy val toSeq: Seq[(String, T)] = toVector

  lazy val toVector: Vector[(String, T)] = {
    val builder = new VectorBuilder[(String, T)]

    for (i ← 0 to elements.length if indexesSet contains i) {
      builder += elements(i)
    }

    builder.result()
  }

  lazy val toIterator: Iterator[(String, T)] = {
    new Iterator[(String, T)] {
      var index = -1
      var nextIndex = -1

      @tailrec def nextIndex(current: Int): Int = {
        val next = current + 1
        if (next >= elements.length) -1
        else if (indexesSet.contains(next)) next
        else nextIndex(next)
      }

      override def hasNext: Boolean = {
        nextIndex = nextIndex(index)
        nextIndex != -1
      }

      override def next(): (String, T) = {
        index = nextIndex
        elements(nextIndex)
      }
    }
  }

  lazy val toIterable: Iterable[(String, T)] = {
    val it = toIterator
    new Iterable[(String, T)] {
      override def iterator: Iterator[(String, T)] = it
    }
  }
}
