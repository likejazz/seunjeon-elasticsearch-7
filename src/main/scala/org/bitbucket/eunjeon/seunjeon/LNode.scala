package org.bitbucket.eunjeon.seunjeon

import scala.collection.JavaConverters._


object LNode {
  def dePreAnalysis(node: LNode): Seq[LNode] =
    if (node.morpheme.getMType == MorphemeType.PREANALYSIS) {
      deComposite(node)
    } else {
      Seq(node)
    }

  def deCompound(node: LNode): Seq[LNode] =
    if (node.morpheme.getMType == MorphemeType.COMPOUND) {
      deComposite(node)
    } else {
      Seq(node)
    }

  def deInflect(node: LNode): Seq[LNode] =
    if (node.morpheme.getMType == MorphemeType.INFLECT) {
      deComposite(node)
    } else {
      Seq(node)
    }

  def deComposite(node: LNode): Seq[LNode] = {
    var nextPos = node.beginOffset
    try {
      val result = node.morpheme.deComposite().
        filterNot(m => isHideMorpheme(m)).
        map { morpheme =>
          val morphemeStartPos = if (isJamo(morpheme.getSurface.head)) nextPos - 1 else nextPos
          val morphemeEndPos = morphemeStartPos + morpheme.getSurface.length
          nextPos = morphemeEndPos
          LNode(morpheme, morphemeStartPos, morphemeEndPos, node.accumulatedCost)
        }
      // 방어코드
      if ((nextPos - node.beginOffset) > node.morpheme.getSurface.length) {
        result.dropRight(1)
      } else result
    } catch {
      // TODO: warning 출력해줄까?
      case _:Throwable => Seq(node)
    }
  }

  private def isHideMorpheme(morpheme: Morpheme): Boolean = {
    morpheme.getSurface == "아" && morpheme.getFeature(0) == "EC"
  }

  private def isJamo(char:Char): Boolean = {
    ('\u1100' <= char && char <= '\u11FF' /* Hangul Jamo */) ||
      ('\u3130' <= char && char <= '\u318F' /* Hangul Compatibility Jamo */)
  }
}

/**
  * Lattice 노드
  * @param morpheme   Morpheme
  * @param beginOffset  시작 offset
  * @param endOffset   끝 offset
  * @param accumulatedCost  누적비용
  */
case class LNode(morpheme:Morpheme,
                 beginOffset: Int, // TODO: startOffset 으로 바꾸자.
                 endOffset: Int,
                 var accumulatedCost:Int = Int.MaxValue) extends OffsetNode {
  var leftNode:LNode = null

  def deCompound(): Seq[LNode] = {
    LNode.deCompound(this)
  }

  def deCompoundJava(): java.util.List[LNode] = {
    deCompound().asJava
  }

  def deInflect(): Seq[LNode] = {
    LNode.deInflect(this)
  }

  def deInflectJava(): java.util.List[LNode] = {
    deInflect().asJava
  }
}
