package org.bitbucket.eunjeon.seunjeon.elasticsearch

import org.bitbucket.eunjeon.seunjeon.{LNode, MorphemeType, Pos, Analyzer}
import org.bitbucket.eunjeon.seunjeon.Pos.Pos
import scala.collection.JavaConverters._


object TokenBuilder {
  val indexPoses = Set[Pos](
    Pos.N,  // 체언
    Pos.SL, // 외국어
    Pos.SH, // 한자
    Pos.SN, // 숫자
    Pos.XR, // 어근
    Pos.V, // 용언
    Pos.UNKNOWN)

  def tokenize(document:String): java.util.List[LuceneToken] = {
    // TODO: 어절, 복합명사 분해
    // TODO: 여러 문장의 offset 계산
    val analyzed = Analyzer.parseEojeol(document)
    analyzed.flatMap { eojeol =>
      val nodes = eojeol.nodes.filter(isIndexNode).flatMap(deCompound)

      // TODO: 어절 색인 옵션으로 뺄까?
      if (eojeol.nodes.length > 1 && nodes.nonEmpty) {
        val eojeolNode = LuceneToken(0, nodes.length, eojeol.startPos, eojeol.endPos, eojeol.surface, "EOJEOL")
        nodes.head +: eojeolNode +: nodes.tail
      } else {
        nodes
      }
    }.asJava
  }

  private def deCompound(node:LNode): Seq[LuceneToken] = {
    // TODO: decompound option으로 뺴야할까?
    if (node.morpheme.mType == MorphemeType.COMPOUND) {
      LNode.deComposite(node).map { n =>
        LuceneToken(1, 1, n.startPos, n.endPos, n.morpheme.surface, n.morpheme.poses.mkString("+"))
      }
    } else {
      LuceneToken(1, 1,
        node.startPos,
        node.endPos,
        node.morpheme.surface,
        node.morpheme.poses.mkString("+")) :: Nil
    }
  }

  private def isIndexNode(node:LNode): Boolean = {
    node.morpheme.mType == MorphemeType.COMPOUND ||
      node.morpheme.mType == MorphemeType.INFLECT ||
      (node.morpheme.mType == MorphemeType.COMMON && indexPoses.contains(node.morpheme.poses.head))
  }
}

case class LuceneToken(
  positionIncr:Int,
  positionLength:Int,
  startOffset:Int,
  endOffset:Int,
  surface:String,
  poses:String)
