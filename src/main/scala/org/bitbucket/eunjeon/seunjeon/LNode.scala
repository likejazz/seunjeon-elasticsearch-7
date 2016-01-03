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
    var startPos = node.startPos
    var endPos = node.endPos
    node.morpheme.deComposite().map { morpheme =>
//    Morpheme.deComposition(node.morpheme.feature(7)).map { morpheme =>
      // TODO: "ㄴ다" 의 경우 startPos 랑 endPos 잘 계산해서 수정해주자.
      //       성능 걱정으로.. 할 수 있을지 모르겠음.
      val morphemeStartPos = startPos
      val morphemeEndPos = startPos+morpheme.surface.length
      startPos = morphemeEndPos
      LNode(morpheme, morphemeStartPos, morphemeEndPos, node.accumulatedCost)
    }
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
                 var accumulatedCost:Int=Short.MaxValue) {
  var leftNode:LNode = null
  var isActive:Boolean=true

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
