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

import scala.collection.mutable
import scala.io.Source
import scala.util.Try
import scala.util.matching.Regex

object LexiconDict {
  val compoundDelimiter = "+"
  val compoundDelimiterRegex: String = "(?<!\\\\)" + Pattern.quote(compoundDelimiter)

  def buildNNGTerm(surface:String, cost:Int): Morpheme= {
    val jongsung = if (isHangul(surface.last)) {
      if (hasJongsung(surface.last)) "T" else "F"
    } else "*"
    val surfaces = surface.split(compoundDelimiterRegex)
    val escapedSurfaces = surfaces.map(_.replaceAllLiterally("\\+", "+"))
    val compositionFeature = if (surfaces.length >= 2) escapedSurfaces.map(_ + "/NNG/*" ).mkString("+") else "*"
    val morphemeType = if (surfaces.length >= 2) "Compound" else "*"

    val feature = Array("NNG","*", jongsung, surfaces.mkString("+"), morphemeType, "*", "*", compositionFeature)
    BasicMorpheme(
      escapedSurfaces.mkString(""),
      NngUtil.nngLeftId,
      NngUtil.nngRightId,
      cost,
      wrapRefArray(feature).mkString(","),
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

  var termDict: Array[Morpheme] = _

  var dictMapper: Array[Array[Int]] = _
  var trie: DoubleArrayTrie = _


  def getDictionaryInfo: String = {
    s"termSize = ${termDict.length} mapper size = ${dictMapper.length}"
  }

  def loadFromFile(file: String, compress: Boolean = false): LexiconDict = {
    val iterator = Source.fromFile(file, "utf-8").getLines()
    loadFromIterator(iterator, compress)
  }

  def loadFromDir(dir: String, compress: Boolean = false): LexiconDict = {
    val r = new Regex(".+[.]csv")
    val files = new File(dir).listFiles.filter(f => r.findFirstIn(f.getName).isDefined)
    val totalIterator:Iterator[String] = files.map(f => Source.fromFile(f, "utf-8").getLines()).reduceLeft(_ ++ _)
    loadFromIterator(totalIterator, compress)
  }

  def loadFromString(str: String, compress: Boolean = false): LexiconDict = {
    val iterator = str.stripMargin.split("\n").toIterator
    loadFromIterator(iterator, compress)
  }

  def csvParse(str: String): List[String] =
    str.split(",(?=([^\"]*\"[^\"]*\")*(?![^\"]*\"))").toList.map(_.replaceFirst("^\"", "").replaceFirst("\"$", ""))

  def loadFromIterator(iterator: Iterator[String], compress: Boolean = false): LexiconDict = {
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
                BasicMorpheme(surface,
                  leftId.toShort,
                  rightId.toShort,
                  cost.toShort,
                  feature.mkString(","),
                  MorphemeType(feature),
                  wrapRefArray(Pos.poses(feature)))
            }
          }
        }.toSeq
    val morphemes: Seq[Morpheme] = parsedLine.filter(_.isSuccess).map(_.get)
    val elapsedTime = (System.nanoTime() - startTime) / (1000*1000)
    logger.info(s"csv parsing is completed. ($elapsedTime ms)")

    build(morphemes.sortBy(_.getSurface), compress)
  }

  private def build(sortedTerms: Seq[Morpheme], termDictCompress: Boolean): LexiconDict = {
    termDict = (if (termDictCompress) sortedTerms.map(x => new CompressedMorpheme(x)) else sortedTerms).toArray
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
      if (preSurface != term.getSurface) {
        if (preSurface != null) {
          groupedTerms.append((preSurface, curIndices))
        }
        curIndices = Array[Int]()
      }
      curIndices = curIndices :+ idx
      preSurface = term.getSurface
    }
    groupedTerms.append((preSurface, curIndices))
    groupedTerms.toArray
  }

  def commonPrefixSearch(keyword: String): Seq[Morpheme] = {
//      trie.commonPrefixSearch(keyword).flatMap(dictMapper(_).map(termDict(_).uncompressed))
    trie.commonPrefixSearch(keyword).flatMap(dictMapper(_).map(termDict(_)))
  }

  def save(termDictPath: String, dictMapperPath: String, triePath: String): Unit = {
    val termDictStore = new ObjectOutputStream(
      new BufferedOutputStream(new FileOutputStream(termDictPath), 16*1024))
    termDictStore.writeObject(termDict.map(x => new CompressedMorpheme(x)))
    termDictStore.close()

    val dictMapperStore = new ObjectOutputStream(
      new BufferedOutputStream(new FileOutputStream(dictMapperPath), 16*1024))
    dictMapperStore.writeObject(dictMapper)
    dictMapperStore.close()

    trie.write(new java.io.File(triePath))

  }

  def load(termDictCompress: Boolean): LexiconDict = {
    logger.info(s"LexiconDict loading... compress mode: $termDictCompress")
    val termDictStream = new BufferedInputStream(classOf[LexiconDict].getResourceAsStream(DictBuilder.TERM_DICT), 32*1024)
    val dictMapperStream = new BufferedInputStream(classOf[LexiconDict].getResourceAsStream(DictBuilder.DICT_MAPPER), 32*1024)
    val trieStream = classOf[LexiconDict].getResourceAsStream(DictBuilder.TERM_TRIE)

    load(termDictStream, dictMapperStream, trieStream, termDictCompress)
    this
  }

  def load(termDictPath: String = DictBuilder.TERM_DICT,
           dictMapperPath: String = DictBuilder.DICT_MAPPER,
           lexiconTriePath: String = DictBuilder.TERM_TRIE,
           termDictCompress: Boolean = false): Unit = {
    val termDictStream = new FileInputStream(termDictPath)
    val dictMapperStream = new FileInputStream(dictMapperPath)
    val trieStream = new FileInputStream(lexiconTriePath)
    load(termDictStream, dictMapperStream, trieStream, termDictCompress)
  }

  private def load(termDictStream: InputStream,
                   dictMapperStream: InputStream,
                   trieStream: InputStream,
                   termDictCompress: Boolean): Unit = {
    // FIXME: 사전 로딩이 3초에서 9초로 느려짐... posid 추가하면서 느려짐..
    var startTime = System.nanoTime()
    val termDictIn = new ObjectInputStream(new BufferedInputStream(termDictStream, 16*1024))
    val compressedMorphemes = termDictIn.readObject().asInstanceOf[Array[Morpheme]]
    termDict = if (termDictCompress) compressedMorphemes else compressedMorphemes.map(x => BasicMorpheme(x))
    termDictIn.close()
    var elapsedTime = (System.nanoTime() - startTime) / (1000*1000)
    logger.info(s"terms loading is completed. ($elapsedTime ms)")

    startTime = System.nanoTime()
    val dictMapperIn = new ObjectInputStream(new BufferedInputStream(dictMapperStream, 16*1024))
    dictMapper = dictMapperIn.readObject().asInstanceOf[Array[Array[Int]]]
    dictMapperIn.close()
    elapsedTime = (System.nanoTime() - startTime) / (1000*1000)
    logger.info(s"mapper loading is completed. ($elapsedTime ms)")

    startTime = System.nanoTime()
    trie = DoubleArrayTrie(trieStream)
    logger.info(s"double-array trie loading is completed. ($elapsedTime ms)")

  }
}
