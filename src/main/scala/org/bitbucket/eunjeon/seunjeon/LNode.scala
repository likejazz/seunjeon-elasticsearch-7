package org.bitbucket.eunjeon.seunjeon


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

  def deComposite(node: LNode): Seq[LNode] = {
    var startPos = node.startPos
    var endPos = node.endPos
    Morpheme.deComposition(node.morpheme.feature(7)).map { morpheme =>
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
case class LNode(morpheme:Morpheme, startPos:Int, endPos:Int, var accumulatedCost:Int = 9999) {
  var leftNode:LNode = null
}
