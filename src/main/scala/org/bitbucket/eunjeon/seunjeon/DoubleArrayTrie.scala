package org.bitbucket.eunjeon.seunjeon

import java.io._

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


object DoubleArrayTrieBuilder {
  def apply() = new DoubleArrayTrieBuilder()
}

case class TNode(children: mutable.Map[Char, TNode] = mutable.Map(), value: Int = -1)

/* this is a very simple trie that only have a add function */
class DoubleArrayTrieBuilder () {
  val root:TNode = TNode()
  var size = 0

  def add(term: String, value: Int): DoubleArrayTrieBuilder = {
    @tailrec
    def add(tNode: TNode, charIndex: Int): Unit = {
      if (charIndex + 1 == term.length) {
        tNode.children.put(term(charIndex), TNode(value = value))
        size += 1
      } else {
        val child = tNode.children.getOrElseUpdate(term(charIndex), TNode())
        add(child, charIndex + 1)
      }
    }

    add(root, 0)
    this
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

  // for build
  private var nextOffset = 1

  // serialized data
  var base:Array[Int] = null
  var check:Array[Int] = null
  var values:Array[Int] = null
  var charMapper = Array.fill[Int](Char.MaxValue)(emptyValue)
  var mapperMaxValue = 0

  private[seunjeon] def trieInfo: String = {
    s"#check: ${check.length}, #checkReal: ${check.count(_ >= 0)}, nextOffset: ${nextOffset}"
  }

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

  @inline
  private def getCharValue(char:Char): Int = {
    val result = charMapper(char)
    if (result == -1) {
      mapperMaxValue += 1
      charMapper(char) = mapperMaxValue
      mapperMaxValue
    } else result
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
    children.foreach { case (c, node) =>
      setCheck(offset + getCharValue(c), basePos)
      values(offset + getCharValue(c)) = node.value
    }
    // 꼭 현재 노드 완성 후에 child 노드 수행해야 함. 한꺼번에 하면 offset이 꼬임
    children.foreach { case (c, node) =>
      if (node.children.nonEmpty) add(offset + getCharValue(c), node.children)
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
    nextOffset = if (result > nextOffset + 500) nextOffset else result
    result
  }

  private def tryPosition(offset:Int, children:mutable.Map[Char, TNode]): Boolean = {
    if (check.length < offset + Char.MaxValue) {
      resizeArrays(check.length*2)
    }
    children.keys.forall(char => check(offset + getCharValue(char)) == emptyValue)
  }

  def commonPrefixSearch(text:String): Seq[Int] = {
    val result = ArrayBuffer[Int]()
    commonPrefixSearchTail(result, startPos, 0, text)
    result
  }

  @tailrec
  private def commonPrefixSearchTail(result:ArrayBuffer[Int], basePos:Int, charIndex: Int, chars:String): Unit = {
    if (charIndex < chars.length) {
      val char = chars(charIndex)
      val offset = base(basePos)
      val childPos = offset + getCharValue(char)
      if (check(childPos) == basePos) {
        if (values(childPos) != -1) {
          result += values(childPos)
        }
        commonPrefixSearchTail(result, childPos, charIndex + 1, chars)
      }
    }
  }

  def write(file:File): Unit = {
    val out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file), 16*1024))
    out.writeInt(base.length)
    base.foreach(out.writeInt)
    check.foreach(out.writeInt)
    values.foreach(out.writeInt)
    charMapper.foreach(out.writeInt)
    out.writeInt(mapperMaxValue)
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

    charMapper = new Array[Int](Char.MaxValue)
    (0 until Char.MaxValue.toInt).foreach(charMapper(_) = in.readInt())

    mapperMaxValue = in.readInt()

    in.close()

    this
  }

  private def setCheck(index: Int, id: Int): Unit = {
    if (nextOffset == index) {
      nextOffset = findNextFreeCheck(nextOffset)
    }
    check(index) = id
  }

  @inline
  private def extendIfNeeded(i: Int): Unit = {
    if (check.length <= i) {
      val newSize = Math.max(Char.MaxValue.toInt, (base.length * 1.5).toInt)
      resizeArrays(newSize)
    }
  }

  private def findFirstFreeCheck(): Int = {
    var i = nextOffset
    while (0 <= check(i) || base(i) != emptyValue) {
      i += 1
    }
    nextOffset = i
    i
  }

  private def findNextFreeCheck(current: Int): Int = {
    val d = check(current) * -1
    assert(d > 0)
    var next = current + d
    extendIfNeeded(next)
    while (check(next) >= 0 && next < check.length) {
      next += 1
    }
    extendIfNeeded(next)
    check(current) = current - next
    next
  }

  /**
    *
    * @param charValues character values
    * @return the first empty slot index - smallest character id
    */
  private def findEmptyOffset(charValues: Iterable[Int]): Int = {
    var empty = findFirstFreeCheck()
    if (charValues.isEmpty) {
      empty
    } else {
      val minChild = charValues.min
      val maxChild = charValues.max
      while (true) {
        val offset = empty - minChild
        extendIfNeeded(offset + maxChild)
        if (charValues.forall(c => check(offset + c) < 0)) return offset
        empty = findNextFreeCheck(empty)
      }
      -1
    }
  }

}
