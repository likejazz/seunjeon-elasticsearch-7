package org.bitbucket.eunjeon.seunjeon.elasticsearch

import org.bitbucket.eunjeon.seunjeon._
import org.bitbucket.eunjeon.seunjeon.Pos.Pos

import scala.collection.JavaConverters._


object TokenizerHelper {
  val lexiconDict: LexiconDict = new LexiconDict().load()
  val connectionCostDict: ConnectionCostDict = new ConnectionCostDict().load()

  val INDEX_POSES: Set[Pos] = Set[Pos](
    Pos.N,  // 체언
    Pos.SL, // 외국어
    Pos.SH, // 한자
    Pos.SN, // 숫자
    Pos.XR, // 어근
    Pos.V,  // 용언
    Pos.M,  // 수식언(관형사, 부사)
    Pos.UNK)

  lazy val INDEX_POSES_JAVA: Array[String] = INDEX_POSES.map(_.toString).toArray
  lazy val ALL_POSES_JAVA: Array[String] = Pos.values.map(_.toString).toArray

  def convertPos(poses: Array[String]): Set[Pos] = {
    poses.map(Pos.withName).toSet
  }

}


class TokenizerHelper(deCompound:Boolean,
                      deInflect:Boolean,
                      indexEojeol:Boolean,
                      posTagging:Boolean,
                      indexPoses:Set[Pos]) {
  def this() {
    this(true, true, true, true, TokenizerHelper.INDEX_POSES)
  }

  val tokenizer: Tokenizer = new Tokenizer(TokenizerHelper.lexiconDict, TokenizerHelper.connectionCostDict)

  def setUserDict(userWords:Array[String]): Unit = {
    tokenizer.setUserDict(new LexiconDict().loadFromIterator(userWords.iterator))
  }

  def setUserDict(file:String): Unit = {
    tokenizer.setUserDict(new LexiconDict().loadFromFile(file))
  }

  def setMaxUnkLength(length:Int): Unit = {
    tokenizer.setMaxUnkLength(length)
  }

  def tokenize(document:String): java.util.List[LuceneToken] = {
    val eojeols = Eojeoler.build(tokenizer.parseText(document, dePreAnalysis=true))
    val deCompounded = if (this.deCompound) eojeols.map(_.deCompound()) else eojeols
    val deInflected = if (this.deInflect) deCompounded.map(_.deInflect()) else deCompounded
    deInflected.flatMap { eojeol =>
      val nodes = eojeol.nodes.filter(isIndexNode).map(LuceneToken(_, posTagging))

      if (this.indexEojeol) {
        if (eojeol.nodes.length > 1 && nodes.nonEmpty) {
          val eojeolNode = LuceneToken(eojeol, nodes.length, posTagging)
          nodes.head +: eojeolNode +: nodes.tail
        } else nodes
      } else nodes
    }.asJava
  }

  private def isIndexNode(node:LNode): Boolean = {
    node.morpheme.mType == MorphemeType.COMPOUND ||
      node.morpheme.mType == MorphemeType.INFLECT ||
      (node.morpheme.mType == MorphemeType.COMMON && indexPoses.contains(node.morpheme.poses.head))
  }

}
