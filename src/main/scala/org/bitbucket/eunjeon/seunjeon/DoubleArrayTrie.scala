package org.bitbucket.eunjeon.seunjeon


/**
  * http://linux.thai.net/~thep/datrie/datrie.html
  */
object DoubleArrayTrie {
  def apply(simpleTrie:SimpleTrie) = new DoubleArrayTrie().build(simpleTrie)
}

class DoubleArrayTrie {
  val ARRAY_INIT_SIZE = 100000

  val base = Array.fill[Int](ARRAY_INIT_SIZE)(-1)
  val check = Array.fill[Int](ARRAY_INIT_SIZE)(-1)

  def build(simpleTrie: SimpleTrie) = {
    val root = simpleTrie.root

    add(0, root.children.asScala.toMap)
    this
  }

  private def add(index:Int, children:Map[Char, TNode]): Unit = {
    children.foreach { child =>
      val char = child._1
      val tnode = child._2
      val offset = index + char.toInt
      check(offset) = index
      base(offset) = tnode.value
      add(offset, child.children)
    }
  }

  private def findPosition(children:Map[Char, TNode]): Int = {
    // TODO: 성능이 느릴 것임...
    // TODO: ARRAY_INIT_SIZE 가 모자랄 경우 늘려줘야 함
    (0 until ARRAY_INIT_SIZE).toStream.filter(offset => tryPosition(offset, children) == true).head
  }

  private def tryPosition(offset:Int, children:Map[Char, TNode]): Boolean = {
    children.forAll(child => check(offset + child._1.toInt) == -1)
  }
}
