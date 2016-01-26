package org.bitbucket.eunjeon.seunjeon

import java.io._

import scala.collection.JavaConverters._

object DoubleArrayTrieBuilder {
  def apply() = new DoubleArrayTrieBuilder()
}

case class TNode(children:java.util.TreeMap[Char, TNode], value:Int)

/* this is trie that only have a add function */
class DoubleArrayTrieBuilder () {
  val root:TNode = TNode(new java.util.TreeMap[Char, TNode](), -1)
  var size = 0

  def add(term:String, value:Int): DoubleArrayTrieBuilder = {
    add(root, term.toCharArray, value)
    this
  }

  private def add(root:TNode, chars:Array[Char], value:Int): Unit = {
    if (chars.length == 0) {
      size += 1
    } else {
      val children = root.children
      val head = chars.head
      if (children.containsKey(head)) {
        val subTrie = children.get(head)
        add(subTrie, chars.tail, value)
      } else {
        val terminalValue = if (chars.length == 1) value else -1
        val subTrie = TNode(new java.util.TreeMap[Char, TNode](), terminalValue)
        children.put(head, subTrie)
        add(subTrie, chars.tail, value)
      }
    }
  }

  def build(): DoubleArrayTrie = {
    DoubleArrayTrie(this)
  }
}

/**
  * http://linux.thai.net/~thep/datrie/datrie.html
  */
object DoubleArrayTrie {
  def apply(simpleTrie: DoubleArrayTrieBuilder) = new DoubleArrayTrie().build(simpleTrie)
  def apply(file:File) =  new DoubleArrayTrie().read(file)
  def apply(inStream:InputStream) = new DoubleArrayTrie().read(inStream)
}

class DoubleArrayTrie {
  val ARRAY_INIT_SIZE = 30000000
  val emptyValue = -1
  val startPos = 0

  var totalSize = 0
  var nextOffset = 0
  var base = Array.fill[Int](ARRAY_INIT_SIZE)(emptyValue)
  var check = Array.fill[Int](ARRAY_INIT_SIZE)(emptyValue)
  var values = Array.fill[Int](ARRAY_INIT_SIZE)(emptyValue)

  def build(simpleTrie: DoubleArrayTrieBuilder) = {
    base(0) = 0
    totalSize = simpleTrie.size
    val root = simpleTrie.root
    add(startPos, root.children.asScala.toMap)
    packArrays()
    this
  }

  private def packArrays(): Unit = {
    val maxPos = getMaxPosition
    base = copyArray(base, maxPos)
    check = copyArray(check, maxPos)
    values = copyArray(values, maxPos)
  }

  private def copyArray(array:Array[Int], size:Int): Array[Int] = {
    val newArray = new Array[Int](size+1)
    Array.copy(array, 0, newArray, 0, size+1)
    newArray
  }

  private def getMaxPosition: Int = {
    (ARRAY_INIT_SIZE-1 to 0 by -1).toStream.filter(base(_) != -1).head
  }

  private def add(basePos:Int, children:Map[Char, TNode]): Int = {
    /**
      * start from basePos. insert 'c'
      *
      *           base     check
      *         --------------------
      *         |        |         | offset
      *         --------------------
      * basePos | offset |         |
      *         --------------------
      *         |        | basePos | childPos = offset + 'c'
      *         --------------------
      *         | ...    | ...     |
      */
    // TODO: recursive 하게 넣고 있는게 정확히 이해가 안됨... 잘 돌아가긴 하는데...
    val offset = findEmptyOffset(children)
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

  private def findEmptyOffset(children:Map[Char, TNode]): Int = {
    val offset = (nextOffset until ARRAY_INIT_SIZE).toStream.filter(tryPosition(_, children)).head
    // TODO: 가끔 4000 이상 offset이 튈때가 있는데 왜 그런지 모르겠음
    nextOffset = if ((offset - nextOffset) > 100) nextOffset + 1 else offset
    offset
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
        // tail recursive right?
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

  def read(file:File): DoubleArrayTrie = {
    read(new FileInputStream(file))
  }

  def read(inStream:InputStream): DoubleArrayTrie = {
    val in = new DataInputStream(new BufferedInputStream(inStream, 16*1024))
    val size = in.readInt()
    base = new Array[Int](size+1)
    (0 until size).foreach(base(_) = in.readInt())
    check = new Array[Int](size+1)
    (0 until size).foreach(check(_) = in.readInt())
    values = new Array[Int](size+1)
    (0 until size).foreach(values(_) = in.readInt())
    in.close()

    this
  }
}
