package org.bitbucket.eunjeon.seunjeon

import java.io._

import scala.collection.JavaConverters._

/**
  * http://linux.thai.net/~thep/datrie/datrie.html
  */
object DoubleArrayTrie {
  def apply(simpleTrie:SimpleTrie) = new DoubleArrayTrie().build(simpleTrie)

  def apply(file:File) = {
    val trie = new DoubleArrayTrie()
    trie.read(file)
    trie
  }

  def apply(inStream:InputStream) = {
    val trie = new DoubleArrayTrie()
    trie.read(inStream)
    trie
  }
}

class DoubleArrayTrie {
  val ARRAY_INIT_SIZE = 30000000
  val emptyValue = -1
  val startPos = 0

  var totalSize = 0
  var size = 0
  var base = Array.fill[Int](ARRAY_INIT_SIZE)(emptyValue)
  var check = Array.fill[Int](ARRAY_INIT_SIZE)(emptyValue)
  var values = Array.fill[Int](ARRAY_INIT_SIZE)(emptyValue)
  val random = scala.util.Random

  def build(simpleTrie: SimpleTrie) = {
    base(0) = 0
    totalSize = simpleTrie.size
    val root = simpleTrie.root
    add(startPos, root.children.asScala.toMap)
    packArrays()
    this
  }

  private def packArrays(): Unit = {
//    val maxPos = math.max(getMaxPosition(), Char.MaxValue)
    val maxPos = getMaxPosition()
    base = copyArray(base, maxPos)
    check = copyArray(check, maxPos)
    values = copyArray(values, maxPos)
  }

  private def copyArray(array:Array[Int], size:Int): Array[Int] = {
    val newArray = new Array[Int](size+1)
    Array.copy(array, 0, newArray, 0, size+1)
    newArray
  }

  private def getMaxPosition(): Int = {
    (ARRAY_INIT_SIZE-1 to 0 by -1).toStream.filter(base(_) != -1).head
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
      *         | ...    | ...     |
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
    size += 1
    println(size)
    offset
  }

  private def findOffset(children:Map[Char, TNode]): Int = {
    (0 until ARRAY_INIT_SIZE).toStream.map(_ * (math.log(size)*10).toInt).filter(tryPosition(_, children)).head
  }

  private def tryPosition(offset:Int, children:Map[Char, TNode]): Boolean = {
    children.toStream.forall(child => check(offset + child._1.toInt) == emptyValue)
  }

  def commonPrefixSearch(text:String): List[Int] = {
    commonPrefixSearchTail(startPos, text)
  }

  private def commonPrefixSearchTail(basePos:Int, chars:String): List[Int] = {
    if (chars.isEmpty) Nil
    else {
      val char = chars.head
      val offset = base(basePos)
      val childPos = offset + char.toInt
      // TODO: 깔끔하게 고치자
      if (childPos > check.length || (check(childPos) != basePos)) Nil // none exist child node
      else {
        // tail recucive?
        val currentValue = if (values(childPos) == -1) Nil else values(childPos) :: Nil
        currentValue ::: commonPrefixSearchTail(childPos, chars.tail)
      }
    }
  }

  def write(file:File): Unit = {
    val out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file), 16*1024))
    out.writeInt(base.length)
    base.foreach(out.writeInt)
    check.foreach(out.writeInt)
    values.foreach(out.writeInt)
    out.close()
  }

  def read(file:File): Unit = {
    read(new FileInputStream(file))
  }

  def read(inStream:InputStream): Unit = {
    val in = new DataInputStream(new BufferedInputStream(inStream, 16*1024))
    val size = in.readInt()
    base = new Array[Int](size+1)
    (0 until size).foreach(base(_) = in.readInt())
    check = new Array[Int](size+1)
    (0 until size).foreach(check(_) = in.readInt())
    values = new Array[Int](size+1)
    (0 until size).foreach(values(_) = in.readInt())
    in.close()

  }
}
