package org.bitbucket.eunjeon.seunjeon.elasticsearch

import org.bitbucket.eunjeon.seunjeon.{Eojeol, Analyzer}
import scala.collection.JavaConverters._


object PosBuilder {
  def tokenize(document:String): java.util.List[LucenePos] = {
    // TODO: 어절, 복합명사 분해
    // TODO: 명사, 어근, 부사 등등 색인어 추출
    // TODO: BOS, EOS 제외
    // TODO: 여러 문장의 offset 계산
    Analyzer.parseEojeol(document).flatMap{ eojeol =>
      eojeol.nodes.sliding(2).map{prePost =>
        val pre = prePost.head
        val post = prePost.last
        // TODO: mkString 성능 괜찮을까?
        LucenePos(1, 1, post.startPos, post.endPos, post.morpheme.surface, post.morpheme.poses.mkString("+"))
      }
    }.asJava
  }
}

case class LucenePos(
  positionIncr:Int,
  positionLength:Int,
  startOffset:Int,
  endOffset:Int,
  surface:String,
  poses:String
)
