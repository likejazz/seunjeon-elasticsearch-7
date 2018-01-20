package org.bitbucket.eunjeon.seunjeon


import java.util

import scala.collection.JavaConverters._


object Eojeol {

  def empty = Eojeol("", -1, -1, Seq.empty)

  def apply(nodes: Seq[LNode]): Eojeol = {
    val surface = nodes.map(_.morpheme.getSurface).mkString
    val startPos = nodes.head.beginOffset
    val endPos = nodes.last.endOffset
    Eojeol(surface, startPos, endPos, nodes)
  }

}

case class Eojeol(surface: String, beginOffset: Int, endOffset: Int, nodes: Seq[LNode]) extends OffsetNode {

  def nodesJava: util.List[LNode] = nodes.asJava

  def deCompound(): Eojeol = Eojeol(surface, beginOffset, endOffset, nodes.flatMap(_.deCompound()))

  def deInflect(): Eojeol = Eojeol(surface, beginOffset, endOffset, nodes.flatMap(_.deInflect()))

  def isEmpty = nodes.isEmpty
}

