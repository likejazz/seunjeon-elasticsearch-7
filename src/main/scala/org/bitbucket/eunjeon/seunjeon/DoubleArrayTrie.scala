package org.bitbucket.eunjeon.seunjeon

import scala.collection.JavaConverters._

/**
  * http://linux.thai.net/~thep/datrie/datrie.html
  */
object DoubleArrayTrie {
  def apply(simpleTrie:SimpleTrie) = new DoubleArrayTrie().build(simpleTrie)
}

class DoubleArrayTrie {
  val ARRAY_INIT_SIZE = 100000
  val startPos = 0

  val base = Array.fill[Int](ARRAY_INIT_SIZE)(-1)
  val check = Array.fill[Int](ARRAY_INIT_SIZE)(-1)
  val values = Array.fill[Int](ARRAY_INIT_SIZE)(-1)

  def build(simpleTrie: SimpleTrie) = {
    base(0) = 0
    val root = simpleTrie.root
    add(startPos, root.children.asScala.toMap)
    this
  }

  private def add(basePos:Int, children:Map[Char, TNode]): Int = {
    /**
      * start from basePos. insert 'c'
      *         --------------------
      *         | base   | check   |
      *         --------------------
      *         |        |         | offset
      *         --------------------
      * basePos | offset |         |
      *         --------------------
      *         |        | basePos | childPos = offset + 'c'
      *         --------------------
      *         | ...    | ...    |
      */
    val offset = findOffset(children)
    children.foreach { child =>
      val char = child._1
      val tnode = child._2
      val checkPos = offset + char.toInt
      check(checkPos) = basePos
      val childOffset = add(checkPos, tnode.children.asScala.toMap)
      base(checkPos) = childOffset
      values(checkPos) = tnode.value
    }
    offset
  }

  private def findOffset(children:Map[Char, TNode]): Int = {
    // TODO: 성능이 느릴 것임...
    (0 until ARRAY_INIT_SIZE).toStream.filter(tryPosition(_, children)).head
  }

  private def tryPosition(offset:Int, children:Map[Char, TNode]): Boolean = {
    // TODO: if (offset + child._1.toInt  > ARRAY_INIT_SIZE) enlength... array
    children.forall(child => check(offset + child._1.toInt) == -1)
  }

  def commonPrefixSearch(text:String): List[Int] = {
    val chars = text.toCharArray
    commonPrefixSearch(startPos, chars) ::: Nil
  }

  def commonPrefixSearch(basePos:Int, chars:Array[Char]): List[Int] = {
    if (chars.isEmpty) Nil
    else {
      val char = chars.head
      val offset = base(basePos)
      val childPos = offset + char.toInt
      if (check(childPos) == basePos) {
        values(childPos) :: commonPrefixSearch(childPos, chars.tail)
      } else {
        Nil
      }
    }
  }

  // TODO: write function
  // TODO: read function
}
