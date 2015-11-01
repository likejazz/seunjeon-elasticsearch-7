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

import com.github.tototoshi.csv.CSVParser
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import org.trie4j.doublearray.MapDoubleArray
import org.trie4j.patricia.MapPatriciaTrie

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.util.matching.Regex

object Term {
  def createUnknownTerm(surface:String, term: Term): Term = {
    new Term(surface,
      term.leftId,
      term.rightId,
      term.cost*surface.length,
      term.feature)
  }
}

case class Term(surface:String,
                leftId:Short,
                rightId:Short,
                cost:Int,
                feature:Seq[String]) {
}

class LexiconDict {
  val logger = Logger(LoggerFactory.getLogger(this.getClass.getName))

  var termDict: Array[Term] = null
  var dictMapper: Array[Array[Int]] = null
  var trie: MapDoubleArray[Int] = null

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

  def loadFromIterator(iterator: Iterator[String]): LexiconDict = {
    val startTime = System.nanoTime()
    val terms = new mutable.MutableList[Term]()
    // TODO: split(",")로는 "," Term 을 읽을수 없어 csv library 를 사용함.
    // 직접 구현해서 library 의존성을 줄였으면 좋겠음.
    iterator.dropWhile(_.head == '#').
      map(line => CSVParser.parse(line, '"', ',', '"')).foreach {
      case Some(List(surface)) =>
        terms += buildNNGTerm(surface, 1000 - (surface.length * 100))
      case Some(List(surface, cost)) =>
        terms += buildNNGTerm(surface, cost.toShort)
      case Some(List(surface, cost, leftId, rightId, feature @ _*)) =>
        terms += Term(surface,
          cost.toShort,
          leftId.toShort,
          rightId.toShort,
          feature)
    }
    val elapsedTime = (System.nanoTime() - startTime) / (1000*1000)
    logger.info(s"csv parsing is completed. ($elapsedTime ms)")

    build(terms.toIndexedSeq.sortBy(_.surface))
  }

  private def buildNNGTerm(surface:String, cost:Int): Term = {
    val lastChar = surface.last
    if (isHangul(lastChar)) {
      if (hasJongsung(lastChar)) {
        Term(surface, NngUtil.nngLeftId, NngUtil.nngTRightId, cost, Seq("NNG","*","T"))
      } else {
        Term(surface, NngUtil.nngLeftId, NngUtil.nngFRightId, cost, Seq("NNG","*","F"))
      }
    } else {
      Term(surface, NngUtil.nngLeftId, NngUtil.nngRightId, cost, Seq("NNG","*","*"))
    }
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

  private def build(sortedTerms: Seq[Term]): LexiconDict = {
    termDict = sortedTerms.toArray
    val startTime = System.nanoTime()
    val surfaceIndexDict = buildSurfaceIndexDict(sortedTerms)

    dictMapper = surfaceIndexDict.map(_._2)

    val elapsedTime = (System.nanoTime() - startTime) / (1000*1000)
    logger.info(s"terms & mapper building is completed. ($elapsedTime ms)")

    trie = buildTrie(surfaceIndexDict)
    this
  }

  def buildTrie(dict:Array[(String, Array[Int])]): MapDoubleArray[Int] = {
    var startTime = System.nanoTime()
    val patricia = new MapPatriciaTrie[Int]
    for (idx <- dict.indices) {
      patricia.insert(dict(idx)._1, idx)
    }
    var elapsedTime = (System.nanoTime() - startTime) / (1000*1000)
    logger.info(s"patricia trie building is completed. ($elapsedTime ms)")

    startTime = System.nanoTime()
    val result = new MapDoubleArray(patricia)
    elapsedTime = (System.nanoTime() - startTime) / (1000*1000)
    logger.info(s"double-array trie building is completed. ($elapsedTime ms)")
    result
  }

  def buildSurfaceIndexDict(sortedTerms: Seq[Term]):Array[(String, Array[Int])] = {
    val groupedTerms:mutable.ListBuffer[(String, Array[Int])] = mutable.ListBuffer()
    var curIndices:Array[Int] = null
    var preSurface:String = null
    sortedTerms.view.zipWithIndex.foreach { case (term:Term, idx) =>
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

  def commonPrefixSearch(keyword: String): Seq[Term] = {
    val indexedLexiconDictPositions = ListBuffer[Int]()
    val iterator = trie.commonPrefixSearchEntries(keyword).iterator()
    while (iterator.hasNext) {
      indexedLexiconDictPositions += iterator.next().getValue
    }

    indexedLexiconDictPositions.
      flatMap(mapPos => dictMapper(mapPos)).
      map(termPos => termDict(termPos))
  }

  def save(termDictPath: String, dictMapperPath: String, triePath: String): Unit = {

    val termDictStore = new ObjectOutputStream(
      new BufferedOutputStream(new FileOutputStream(termDictPath), 16*1024))
    termDictStore.writeObject(termDict)
    termDictStore.close()

    val dictMapperStore = new ObjectOutputStream(
      new BufferedOutputStream(new FileOutputStream(dictMapperPath), 16*1024))
    dictMapperStore.writeObject(dictMapper)
    dictMapperStore.close()

    // TODO: writer 사용해서 직렬화하자.
    // https://github.com/takawitter/trie4j/blob/master/trie4j/src/test/java/org/trie4j/io/TrieWriterTest.java
    val trieStore = new ObjectOutputStream(
      new BufferedOutputStream(new FileOutputStream(triePath), 16*1024))
    trieStore.writeObject(trie)
    trieStore.close()
  }

  def load(): LexiconDict = {
    val termDictStream = getClass.getResourceAsStream(DictBuilder.TERM_DICT)
    val dictMapperStream = getClass.getResourceAsStream(DictBuilder.DICT_MAPPER)
    val trieStream = getClass.getResourceAsStream(DictBuilder.TERM_TRIE)

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
    var startTime = System.nanoTime()
    val termDictIn = new ObjectInputStream(
      new BufferedInputStream(termDictStream, 16*1024))
    termDict = termDictIn.readObject().asInstanceOf[Array[Term]]
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
    val TrieIn = new ObjectInputStream(new BufferedInputStream(trieStream, 16*1024))
    trie = TrieIn.readObject().asInstanceOf[MapDoubleArray[Int]]
    TrieIn.close()
    elapsedTime = (System.nanoTime() - startTime) / (1000*1000)
    logger.info(s"double-array trie loading is completed. ($elapsedTime ms)")
  }
}
