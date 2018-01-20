package org.bitbucket.eunjeon.seunjeon.elasticsearch

import org.bitbucket.eunjeon.seunjeon.{Eojeol, LNode}


object LuceneToken {

  def apply(lnode: LNode, taggingPos: Boolean): LuceneToken = {
    val poses = lnode.morpheme.getPoses.mkString("+")
    LuceneToken(
      buildCharTerm(lnode.morpheme.getSurface, poses, taggingPos),
      1, 1,
      lnode.beginOffset, lnode.endOffset,
      poses)
  }

  def apply(eojeol:Eojeol, positionIncr: Int, posistionLength:Int, taggingPos:Boolean) : LuceneToken = {
    val eojeolTag = "EOJ"
    LuceneToken(
      buildCharTerm(eojeol.surface, eojeolTag, taggingPos),
      positionIncr, posistionLength,
      eojeol.beginOffset, eojeol.endOffset,
      eojeolTag)
  }

  def buildCharTerm(surface:String, poses:String, taggingPos:Boolean): String = {
    if (taggingPos) s"$surface/$poses"
    else surface
  }
}

case class LuceneToken(charTerm:String,
                       positionIncr:Int,
                       positionLength:Int,
                       beginOffset:Int,
                       endOffset:Int,
                       poses:String)

