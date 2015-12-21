package org.bitbucket.eunjeon.seunjeon.elasticsearch

import com.sun.deploy.util.OrderedHashSet
import org.bitbucket.eunjeon.seunjeon.{LNode, MorphemeType, Pos, Analyzer}
import org.bitbucket.eunjeon.seunjeon.Pos.Pos
import scala.collection.JavaConverters._
import scala.collection.{mutable, SortedSet}


object PosBuilder {
  val indexPoses = Set[Pos](
    Pos.N,  // 체언
    Pos.SL, // 외국어
    Pos.SH, // 한자
    Pos.SN, // 숫자
    Pos.XR, // 어근
    Pos.V, // 용언
    Pos.UNKNOWN)

  def tokenize(document:String): java.util.List[LucenePos] = {
    // TODO: 어절, 복합명사 분해
    // TODO: 여러 문장의 offset 계산
    Analyzer.parseEojeol(document).flatMap { eojeol =>
      val newEojeol = if (eojeol.nodes.length > 1) {
        Seq(LucenePos(0, 1, eojeol.startPos, eojeol.endPos, eojeol.surface, "EOJEOL"))
      } else {
        Seq()
      }
      eojeol.nodes.filter(isIndexNode).map { node =>
        LucenePos(1, 1,
          node.startPos,
          node.endPos,
          node.morpheme.surface,
          node.morpheme.poses.mkString("+"))
      } ++ newEojeol
    }.asJava
  }

  def isIndexNode(node:LNode): Boolean = {
    node.morpheme.mType == MorphemeType.COMPOUND ||
      (node.morpheme.mType == MorphemeType.COMMON && indexPoses.contains(node.morpheme.poses.head))
  }
}

case class LucenePos(
  positionIncr:Int,
  positionLength:Int,
  startOffset:Int,
  endOffset:Int,
  surface:String,
  poses:String
) extends Ordered[LucenePos] {

  // 잘 동작하나???
  override def compare(that: LucenePos): Int = {
    if (startOffset == that.startOffset) {
      if (endOffset == that.endOffset) {
        0
      } else if (endOffset < that.endOffset) {
        1
      } else {
        -1
      }
    } else if (startOffset < that.startOffset) {
      1
    } else {
      -1
    }
  }
}
