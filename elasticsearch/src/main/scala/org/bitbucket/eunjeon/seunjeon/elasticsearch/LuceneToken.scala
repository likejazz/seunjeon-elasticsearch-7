package org.bitbucket.eunjeon.seunjeon.elasticsearch

import org.bitbucket.eunjeon.seunjeon.{Eojeol, LNode}


object LuceneToken {
  def apply(lnode:LNode, taggingPos:Boolean): LuceneToken = {
    LuceneToken(
      buildCharTerm(lnode.morpheme.surface, lnode.morpheme.poses.mkString("+"), taggingPos),
      1, 1,
      lnode.startPos, lnode.endPos,
      lnode.morpheme.poses.mkString("+"))
  }

  def apply(eojeol:Eojeol, nodeLength:Int, taggingPos:Boolean) : LuceneToken = {
    val eojeolTag = "EOJ"
    LuceneToken(
      buildCharTerm(eojeol.surface, eojeolTag, taggingPos),
      0, nodeLength,
      eojeol.startPos, eojeol.endPos,
      eojeolTag)
  }

  def buildCharTerm(surface:String, poses:String, taggingPos:Boolean): String = {
    if (taggingPos) s"$surface/$poses" else surface
  }
}

case class LuceneToken(charTerm:String,
                       positionIncr:Int, positionLength:Int,
                       startOffset:Int, endOffset:Int,
                       poses:String)

