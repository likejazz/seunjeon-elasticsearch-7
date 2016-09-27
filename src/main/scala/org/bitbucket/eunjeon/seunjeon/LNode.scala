package org.bitbucket.eunjeon.seunjeon

import scala.collection.JavaConverters._


object LNode {
  def dePreAnalysis(node: LNode): Seq[LNode] =
    if (node.morpheme.mType == MorphemeType.PREANALYSIS) {
      deComposite(node)
    } else {
      Seq(node)
    }

  def deCompound(node: LNode): Seq[LNode] =
    if (node.morpheme.mType == MorphemeType.COMPOUND) {
      deComposite(node)
    } else {
      Seq(node)
    }

  def deInflect(node: LNode): Seq[LNode] =
    if (node.morpheme.mType == MorphemeType.INFLECT) {
      deComposite(node)
    } else {
      Seq(node)
    }

  def deComposite(node: LNode): Seq[LNode] = {
    var nextPos = node.startPos
    try {
      val result = node.morpheme.deComposite().
        filterNot(m => isHideMorpheme(m)).
        map { morpheme =>
          val morphemeStartPos = if (isJamo(morpheme.surface.head)) nextPos - 1 else nextPos
          val morphemeEndPos = morphemeStartPos + morpheme.surface.length
          nextPos = morphemeEndPos
          LNode(morpheme, morphemeStartPos, morphemeEndPos, node.accumulatedCost)
        }
      // 방어코드
      if ((nextPos - node.startPos) > node.morpheme.surface.length) {
        result.dropRight(1)
      } else result
    } catch {
      // TODO: warning 출력해줄까?
      case _:Throwable => Seq(node)
    }
  }

  private def isHideMorpheme(morpheme: Morpheme): Boolean = {
    morpheme.surface == "아" && morpheme.feature(0) == "EC"
  }

  private def isJamo(char:Char): Boolean = {
    ('\u1100' <= char && char <= '\u11FF' /* Hangul Jamo */) ||
      ('\u3130' <= char && char <= '\u318F' /* Hangul Compatibility Jamo */)
  }
}

/**
  * Lattice 노드
  * @param morpheme   Morpheme
  * @param startPos  시작 offset
  * @param endPos   끝 offset
  * @param accumulatedCost  누적비용
  */
case class LNode(morpheme:Morpheme,
                 var startPos:Int,
                 var endPos:Int,
                 var accumulatedCost:Int = Int.MaxValue) {
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
