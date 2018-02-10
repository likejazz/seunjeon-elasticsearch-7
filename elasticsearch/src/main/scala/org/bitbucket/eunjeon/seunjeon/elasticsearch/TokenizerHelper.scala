package org.bitbucket.eunjeon.seunjeon.elasticsearch

import java.util
import java.util.InvalidPropertiesFormatException

import org.apache.logging.log4j.Logger
import org.bitbucket.eunjeon.seunjeon._
import org.bitbucket.eunjeon.seunjeon.Pos.Pos
import org.elasticsearch.common.logging.ESLoggerFactory

import scala.collection.JavaConverters._


object TokenizerHelper {
  val logger: Logger = ESLoggerFactory.getLogger(TokenizerHelper.getClass.getName)

  val compress: Boolean = System.getProperty("seunjeon.compress") match {
    case "true" => true
    case "false" => false
    case null => Runtime.getRuntime.maxMemory() <= 1024 * 1024 * 1024  // 1g
    case x => throw new InvalidPropertiesFormatException(s"'eunjeon.compress' expect true or false. but got '$x'")
  }
  logger.info(s"seunjeon lexiconDict compress: $compress")

  lazy val lexiconDict: LexiconDict =
    if (compress) new LexiconDict().load(termDictCompress = true)
    else new LexiconDict().load(termDictCompress = false)

  lazy val connectionCostDict: ConnectionCostDict = new ConnectionCostDict().load()

  val INDEX_POSES: Set[Pos] = Set[Pos](
    Pos.N,  // 체언
    Pos.SL, // 외국어
    Pos.SH, // 한자
    Pos.SN, // 숫자
    Pos.XR, // 어근
    Pos.V,  // 용언
    Pos.M,  // 수식언(관형사, 부사)
    Pos.UNK)

  lazy val INDEX_POSES_JAVA: util.List[String] = INDEX_POSES.map(_.toString).toList.asJava
  lazy val ALL_POSES_JAVA: util.List[String] = Pos.values.map(_.toString).toList.asJava

  def convertPos(poses: util.List[String]): Set[Pos] = poses.asScala.map(Pos.withName).toSet

  def toLuceneTokens(eojeols: Iterable[Eojeol], indexEojeol: Boolean, posTagging: Boolean): Iterable[LuceneToken] = {
    eojeols.flatMap { eojeol: Eojeol =>
      val luceneTokens = eojeol.nodes.map { node =>
        val poses = node.morpheme.getPoses.mkString("+")
        val surface = if (posTagging) s"${node.morpheme.getSurface}/${poses}" else node.morpheme.getSurface
        LuceneToken(surface, 1, 1, node.beginOffset, node.endOffset, node.morpheme.getPoses.mkString("+"))
      }

      if (indexEojeol) { mergeEojeol(luceneTokens, eojeol, posTagging) } else luceneTokens
    }
  }

  def mergeEojeol(tokens: Seq[LuceneToken], eojeol: Eojeol, posTagging: Boolean): Seq[LuceneToken] = {
    if (tokens.isEmpty) Seq.empty[LuceneToken]
    else {
      val eojeolPoses = "EOJ"
      val eojeolSurface = if (posTagging) s"${eojeol.surface}/${eojeolPoses}" else eojeol.surface

      val eojeolIdx = getEojeolIdx(tokens, eojeol)
      if (eojeolIdx == 0) {
        Some(LuceneToken(eojeolSurface, 1, eojeol.nodes.size, eojeol.beginOffset, eojeol.endOffset, eojeolPoses)).toSeq ++
          tokens.headOption.map(x => LuceneToken(x.charTerm, 0, x.positionLength, x.beginOffset, x.endOffset, x.poses)) ++
          tokens.tail
      } else {
        val preTokens = tokens.slice(0, eojeolIdx)
        val postTokens = tokens.slice(eojeolIdx, tokens.length)
        val eojeolToken =
          if (preTokens.last.beginOffset == eojeol.beginOffset && preTokens.last.endOffset == eojeol.endOffset) None
          else Some(LuceneToken(eojeolSurface, 0, eojeol.nodes.size, eojeol.beginOffset, eojeol.endOffset, eojeolPoses))
        preTokens ++ eojeolToken ++ postTokens
      }
    }
  }

  private def isSameOffset(tokens: Seq[LuceneToken], eojeol: Eojeol) = {
    tokens.head.beginOffset == eojeol.beginOffset && tokens.head.endOffset == eojeol.endOffset
  }

  private def getEojeolIdx(tokens: Seq[LuceneToken], eojeol: Eojeol): Int = {
    val splitIdx = tokens.indexWhere(node => node.beginOffset > eojeol.beginOffset)
    if (splitIdx == -1) tokens.length else splitIdx
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

  val tokenizer: Tokenizer =
    new Tokenizer(TokenizerHelper.lexiconDict, TokenizerHelper.connectionCostDict, TokenizerHelper.compress)

  def setUserDict(userWords: util.List[String]): Unit = {
    tokenizer.setUserDict(new LexiconDict().loadFromIterator(userWords.asScala.iterator))
  }

  def setUserDict(file: String): Unit = {
    tokenizer.setUserDict(new LexiconDict().loadFromFile(file))
  }

  def setMaxUnkLength(length:Int): Unit = {
    tokenizer.setMaxUnkLength(length)
  }

  def tokenize(document:String): java.lang.Iterable[LuceneToken] = {
    val eojeols: Iterable[Eojeol] = Eojeoler.build(tokenizer.parseText(document, dePreAnalysis=true)).flatMap(_.eojeols)
    val deCompounded: Iterable[Eojeol] = if (this.deCompound) eojeols.map(_.deCompound()) else eojeols
    val deInflected: Iterable[Eojeol] = if (this.deInflect) deCompounded.map(_.deInflect()) else deCompounded
    val posFiltered: Iterable[Eojeol] = deInflected.map { eojeol =>
      Eojeol(eojeol.surface, eojeol.beginOffset, eojeol.endOffset, eojeol.nodes.filter(isIndexNode))
    }

    val luceneTokens = TokenizerHelper.toLuceneTokens(posFiltered, this.indexEojeol, this.posTagging)

    luceneTokens.asJava
  }

  private def isIndexNode(node:LNode): Boolean = {
    node.morpheme.getMType == MorphemeType.COMPOUND ||
      node.morpheme.getMType == MorphemeType.INFLECT ||
      (node.morpheme.getMType == MorphemeType.COMMON && indexPoses.contains(node.morpheme.getPoses.head))
  }


}
