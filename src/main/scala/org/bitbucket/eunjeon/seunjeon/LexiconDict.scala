/**
 * Copyright 2015 youngho yu, yongwoon lee
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package org.bitbucket.eunjeon.seunjeon

import java.io.{File, _}
import java.util.regex.Pattern

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import scala.collection.parallel.immutable.ParVector
import scala.collection.mutable
import scala.io.Source
import scala.util.Try
import scala.util.matching.Regex

object LexiconDict {
  val compoundDelimiter = "+"
  val compoundDelimiterRegex = "(?<!\\\\)" + Pattern.quote(compoundDelimiter)

  def buildNNGTerm(surface:String, cost:Int): Morpheme = {
    val jongsung = if (isHangul(surface.last)) {
      if (hasJongsung(surface.last)) "T" else "F"
    } else "*"
    val surfaces = surface.split(compoundDelimiterRegex)
    val escapedSurfaces = surfaces.map(_.replaceAllLiterally("\\+", "+"))
    val compositionFeature = if (surfaces.length >= 2) escapedSurfaces.map(_ + "/NNG/*" ).mkString("+") else "*"
    val morphemeType = if (surfaces.length >= 2) "Compound" else "*"

    val feature = Array("NNG","*", jongsung, surfaces.mkString("+"), morphemeType, "*", "*", compositionFeature)
    Morpheme(
      escapedSurfaces.mkString(""),
      NngUtil.nngLeftId,
      NngUtil.nngRightId,
      cost,
      wrapRefArray(feature),
      MorphemeType(feature),
      wrapRefArray(Pos.poses(feature)))
  }

  private def hasJongsung(ch:Char): Boolean = {
    if (((ch - 0xAC00) % 0x001C) == 0) {
      false
    } else {
      true
    }
  }

  private def isHangul(ch:Char): Boolean = {
    if ((0x0AC00 <= ch && ch <= 0xD7A3)
      || (0x1100 <= ch && ch <= 0x11FF)
      || (0x3130 <= ch && ch <= 0x318F)) {
      true
    } else {
      false
    }
  }
}


class LexiconDict {
  val logger = Logger(LoggerFactory.getLogger(classOf[LexiconDict].getName))

  var termDict: Array[CompressedMorpheme] = null

  var dictMapper: Array[Array[Int]] = null
  var trie: DoubleArrayTrie = null


  def getDictionaryInfo(): String = {
    s"termSize = ${termDict.length} mapper size = ${dictMapper.length}"
  }

  def loadFromFile(file: String): LexiconDict = {
    val iterator = Source.fromFile(file, "utf-8").getLines()
    loadFromIterator(iterator)
  }

  def loadFromDir(dir: String): LexiconDict = {
    val r = new Regex(".+[.]csv")
    val files = new File(dir).listFiles.filter(f => r.findFirstIn(f.getName).isDefined)
    val totalIterator:Iterator[String] = files.map(f => Source.fromFile(f, "utf-8").getLines()).reduceLeft(_ ++ _)
    loadFromIterator(totalIterator)
  }

  def loadFromString(str: String): LexiconDict = {
    val iterator = str.stripMargin.split("\n").toIterator
    loadFromIterator(iterator)
  }

  def csvParse(str: String): List[String] =
    str.split(",(?=([^\"]*\"[^\"]*\")*(?![^\"]*\"))").toList.map(_.replaceFirst("^\"", "").replaceFirst("\"$", ""))

  def loadFromIterator(iterator: Iterator[String]): LexiconDict = {
    val startTime = System.nanoTime()
    val parsedLine: Seq[Try[Morpheme]] =
      iterator.dropWhile(_.head == '#').
        map(_.replaceAll(" ", "")).
        map(csvParse).
        map { x =>
          Try {
            x match {
              // "단어"
              case List(surface) =>
                LexiconDict.buildNNGTerm(surface, 1000 - (surface.length * 100))
              // "단어,-100"  # 단어,비용
              case List(surface, cost) =>
                LexiconDict.buildNNGTerm(surface, cost.toShort)
              case List(surface, leftId, rightId, cost, feature@_ *) =>
                Morpheme(surface,
                  leftId.toShort,
                  rightId.toShort,
                  cost.toShort,
                  wrapRefArray(feature.toArray),
                  MorphemeType(feature),
                  wrapRefArray(Pos.poses(feature)))
            }
          }
        }.toSeq
    val morphemes: Seq[Morpheme] = parsedLine.filter(_.isSuccess).map(_.get)
    val elapsedTime = (System.nanoTime() - startTime) / (1000*1000)
    logger.info(s"csv parsing is completed. ($elapsedTime ms)")

    build(morphemes.sortBy(_.surface))
  }

  private def build(sortedTerms: Seq[Morpheme]): LexiconDict = {

    termDict = CompressedMorpheme.compress(sortedTerms)
    val startTime = System.nanoTime()
    val surfaceIndexDict = buildSurfaceIndexDict(sortedTerms)

    dictMapper = surfaceIndexDict.map(_._2)

    val elapsedTime = (System.nanoTime() - startTime) / (1000*1000)
    logger.info(s"terms & mapper building is completed. ($elapsedTime ms)")

    trie = buildTrie(surfaceIndexDict)
    this
  }

  def buildTrie(dict:Array[(String, Array[Int])]): DoubleArrayTrie = {
    var startTime = System.nanoTime()
    val trieBuilder = DoubleArrayTrieBuilder()
    for (idx <- dict.indices) {
      trieBuilder.add(dict(idx)._1, idx)
    }
    var elapsedTime = (System.nanoTime() - startTime) / (1000*1000)
    logger.info(s"added to trie builder ($elapsedTime ms)")

    startTime = System.nanoTime()
    val doubleArrayTrie = trieBuilder.build()
    elapsedTime = (System.nanoTime() - startTime) / (1000*1000)
    logger.info(s"double-array trie building is completed. ($elapsedTime ms)")
    doubleArrayTrie
  }

  def buildSurfaceIndexDict(sortedTerms: Seq[Morpheme]):Array[(String, Array[Int])] = {
    val groupedTerms:mutable.ListBuffer[(String, Array[Int])] = mutable.ListBuffer()
    if (sortedTerms.isEmpty) {
      return groupedTerms.toArray
    }

    var curIndices:Array[Int] = null
    var preSurface:String = null
    sortedTerms.view.zipWithIndex.foreach { case (term:Morpheme, idx) =>
      if (preSurface != term.surface) {
        if (preSurface != null) {
          groupedTerms.append((preSurface, curIndices))
        }
        curIndices = Array[Int]()
      }
      curIndices = curIndices :+ idx
      preSurface = term.surface
    }
    groupedTerms.append((preSurface, curIndices))
    groupedTerms.toArray
  }

  def commonPrefixSearch(keyword: String): Seq[Morpheme] = {
      trie.commonPrefixSearch(keyword).flatMap(dictMapper(_).map(termDict(_).uncompressed))
  }

  def save(termDictPath: String, dictMapperPath: String, triePath: String): Unit = {

    val termDictStore =
      new BufferedOutputStream(new FileOutputStream(termDictPath), 32*1024)
    CompressionHelper.compressObjectAndSave(termDict, termDictStore)
    termDictStore.close()

    val dictMapperStore =
      new BufferedOutputStream(new FileOutputStream(dictMapperPath), 32*1024)
    CompressionHelper.compressObjectAndSave(dictMapper, dictMapperStore)
    dictMapperStore.close()

    trie.write(new java.io.File(triePath))
  }

  def load(): LexiconDict = {

    val termDictStream = new BufferedInputStream(classOf[LexiconDict].getResourceAsStream(DictBuilder.TERM_DICT), 32*1024)
    val dictMapperStream = new BufferedInputStream(classOf[LexiconDict].getResourceAsStream(DictBuilder.DICT_MAPPER), 32*1024)
    val trieStream = classOf[LexiconDict].getResourceAsStream(DictBuilder.TERM_TRIE)

    load(termDictStream, dictMapperStream, trieStream)
    this
  }

  def load(termDictPath: String = DictBuilder.TERM_DICT,
           dictMapperPath: String = DictBuilder.DICT_MAPPER,
           lexiconTriePath: String = DictBuilder.TERM_TRIE): Unit = {
    val termDictStream = new FileInputStream(termDictPath)
    val dictMapperStream = new FileInputStream(dictMapperPath)
    val trieStream = new FileInputStream(lexiconTriePath)
    load(termDictStream, dictMapperStream, trieStream)
  }

  private def load(termDictStream: InputStream,
                   dictMapperStream: InputStream,
                   trieStream: InputStream): Unit = {
    // FIXME: 사전 로딩이 3초에서 9초로 느려짐... posid 추가하면서 느려짐..

    /* creating parallel vector containing the termDictStream, dictMapperStream so that they can
     be loaded parallel by decompressing them */
    val pv: ParVector[InputStream] = ParVector(termDictStream, dictMapperStream).par
    var startTime = System.nanoTime()
    val uncompressedObjects = pv.map(CompressionHelper.uncompressAndReadObject(_).asInstanceOf[Any])
    var elapsedTime = System.nanoTime() - startTime
    logger.info(s"Loading termDict and dictMapper from archives is completed. ($elapsedTime ns)")

    termDict = uncompressedObjects(0).asInstanceOf[Array[CompressedMorpheme]]
    dictMapper = uncompressedObjects(1).asInstanceOf[Array[Array[Int]]]

    startTime = System.nanoTime()
    trie = DoubleArrayTrie(trieStream)
    elapsedTime = System.nanoTime() - startTime
    logger.info(s"double-array trie loading is completed. ($elapsedTime ns)")
  }
}
