package org.bitbucket.eunjeon.seunjeon

case class TNode(children:java.util.TreeMap[Char, TNode], value:Int)

object SimpleTrie {
  def apply() = new SimpleTrie()
}

class SimpleTrie() {
  val root:TNode = TNode(new java.util.TreeMap[Char, TNode](), -1)
  var size = 0

  def add(term:String, value:Int): Unit = {
    add(root, term.toCharArray, value)
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
}
