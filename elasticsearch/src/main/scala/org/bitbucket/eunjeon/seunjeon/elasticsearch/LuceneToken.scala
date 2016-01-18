package org.bitbucket.eunjeon.seunjeon.elasticsearch

import org.bitbucket.eunjeon.seunjeon.LNode


object LuceneToken {
  // TODO: posTagging
  def apply(lnode:LNode): LuceneToken = {
    LuceneToken(
      s"${lnode.morpheme.surface}/${lnode.morpheme.poses.mkString("+")}",
      1, 1,
      lnode.startPos, lnode.endPos,
      lnode.morpheme.poses.mkString("+"))
  }
}

// TODO: posTagging
case class LuceneToken(charTerm:String,
                       positionIncr:Int, positionLength:Int,
                       startOffset:Int, endOffset:Int,
                       poses:String)

