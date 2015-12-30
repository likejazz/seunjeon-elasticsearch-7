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
    val analyzed = Analyzer.parseEojeol(document).map(_.deCompound()).map(_.deInflect())
    analyzed.flatMap { eojeol =>
      val nodes = eojeol.nodes.filter(isIndexNode).map(LuceneToken(_))

      // TODO: 어절 색인 옵션으로 뺄까?
      if (eojeol.nodes.length > 1 && nodes.nonEmpty) {
        val eojeolNode = LuceneToken(s"${eojeol.surface}", 0, nodes.length, eojeol.startPos, eojeol.endPos, "EOJEOL")
        nodes.head +: eojeolNode +: nodes.tail
      } else {
        nodes
      }
    }.asJava
  }

  private def isIndexNode(node:LNode): Boolean = {
    node.morpheme.mType == MorphemeType.COMPOUND ||
      node.morpheme.mType == MorphemeType.INFLECT ||
      (node.morpheme.mType == MorphemeType.COMMON && indexPoses.contains(node.morpheme.poses.head))
  }
}

