package sangria.marshalling

import scala.collection.immutable.{VectorBuilder, ListMap}
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
}
