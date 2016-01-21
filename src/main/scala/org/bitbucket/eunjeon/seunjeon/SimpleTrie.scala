package org.bitbucket.eunjeon.seunjeon

import scala.collection.mutable

case class TNode(children:java.util.TreeMap[Char, TNode], value:Int)

object SimpleTrie {
  def apply() = new SimpleTrie()
}

class SimpleTrie() {
  val root:TNode = TNode(new java.util.TreeMap[Char, TNode](), -1)

  def add(term:String, value:Int): Unit = {
    add(root, term.toCharArray, value)
  }

  private def add(root:TNode, chars:Array[Char], value:Int): Unit = {
    if (chars.length == 0) {
    } else {
      val children = root.children
      val head = chars.head
      if (children.containsKey(head)) {
        val subTrie = children.get(head)
        add(subTrie, chars.tail, value)
      } else {
        val termianlValue = if (chars.length == 1) value else -1
        val subTrie = TNode(new java.util.TreeMap[Char, TNode](), termianlValue)
        children.put(head, subTrie)
        add(subTrie, chars.tail, value)
      }
    }
  }
//
//  def commonPrefixSearch(term:String): Seq[Int] = {
//    commonPrefixSearch(root, term.toCharArray)
//  }
//
//  private def commonPrefixSearch(root:TNode, chars:Array[Char]): Seq[Int] = {
//
//  }

}
