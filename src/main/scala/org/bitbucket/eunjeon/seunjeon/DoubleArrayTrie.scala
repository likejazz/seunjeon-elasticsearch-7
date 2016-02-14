package org.bitbucket.eunjeon.seunjeon

import java.io._
import scala.collection.mutable


object DoubleArrayTrieBuilder {
  def apply() = new DoubleArrayTrieBuilder()
}


case class TNode(children:mutable.Map[Char, TNode], value:Int)

/* this is a very simple trie that only have a add function */
class DoubleArrayTrieBuilder () {
  val root:TNode = TNode(mutable.Map[Char, TNode](), -1)
  var size = 0

  def add(term:String, value:Int): DoubleArrayTrieBuilder = {
    add(root, term.toCharArray, value)
    this
  }

  private def add(tNode:TNode, chars:Array[Char], value:Int): Unit = {
    if (chars.length == 0) {
      size += 1
    } else {
      val children = tNode.children
      val head = chars.head
      if (children.contains(head)) {
        val subTrie = children.getOrElse(head, null)
        add(subTrie, chars.tail, value)
      } else {
        val terminalValue = if (chars.length == 1) value else -1
        val subTrie = TNode(mutable.Map[Char, TNode](), terminalValue)
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
  val ARRAY_INIT_SIZE = Char.MaxValue.toInt
  val emptyValue = -1
  val startPos = 0

  var nextOffset = 0
  var base:Array[Int] = null
  var check:Array[Int] = null
  var values:Array[Int] = null

  def build(simpleTrie: DoubleArrayTrieBuilder) = {
    base = Array.fill[Int](simpleTrie.size+ARRAY_INIT_SIZE)(emptyValue)
    check = Array.fill[Int](simpleTrie.size+ARRAY_INIT_SIZE)(emptyValue)
    values = Array.fill[Int](simpleTrie.size+ARRAY_INIT_SIZE)(emptyValue)
    base(0) = 0
    val root = simpleTrie.root
    add(startPos, root.children)
    packArrays()
    this
  }

  private def packArrays(): Unit = {
    if (nextOffset+Char.MaxValue < check.length)
      resizeArrays(nextOffset+Char.MaxValue)
  }


  private def add(basePos:Int, children:mutable.Map[Char, TNode]): Unit = {
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
    val offset = findEmptyOffset(children)
    base(basePos) = offset
    children.foreach { child =>
      val char = child._1
      val tnode = child._2
      val checkPos = offset + char.toInt
      check(checkPos) = basePos
      values(checkPos) = tnode.value
    }
    // 꼭 현재 노드 완성 후에 child 노드 수행해야 함. 한꺼번에 하면 offset이 꼬임
    children.foreach { child =>
      val char = child._1
      val tnode = child._2
      val checkPos = offset + char.toInt
      add(checkPos, tnode.children)
    }
  }

  private def resizeArrays(size:Int): Unit = {
//    println(s"resize array from ${base.length} to $size")
    base = resizeArray(base, size)
    check = resizeArray(check, size)
    values = resizeArray(values, size)
  }

  private def resizeArray(array:Array[Int], size:Int): Array[Int] = {
    val newArray = Array.fill[Int](size)(emptyValue)
    Array.copy(array, 0, newArray, 0, Math.min(array.length, size))
    newArray
  }

  private def findEmptyOffset(children:mutable.Map[Char, TNode]): Int = {
    val result = (nextOffset to check.length).toStream.filter(tryPosition(_, children)).head
    nextOffset = if (result > nextOffset + 100) nextOffset else result
    result
  }

  private def tryPosition(offset:Int, children:mutable.Map[Char, TNode]): Boolean = {
    if (check.length < offset + Char.MaxValue) {
      resizeArrays(check.length*2)
    }
    children.keys.forall(char => check(offset + char.toInt) == emptyValue)
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
