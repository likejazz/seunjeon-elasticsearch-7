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

import org.trie4j.doublearray.MapDoubleArray
import org.trie4j.patricia.MapPatriciaTrie

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.util.control.NonFatal
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
                feature:String) {
}

object LexiconDict {
  val termDictResourceFile = "/termDict.dat"
  val dictMapperResourceFile = "/dicMapper.dat"
  val trieResourceFile = "/trie.dat"
}

class LexiconDict {
  var termDict: Array[Term] = null
  var dictMapper: Array[Array[Int]] = null
  var trie: MapDoubleArray[Int] = null

  def loadFromCsvFiles(dir: String): Unit = {
    val r = new Regex(".+[.]csv")
    val files = new File(dir).listFiles.filter(f => r.findFirstIn(f.getName).isDefined)
    val totalIterator:Iterator[String] = files.map(f => Source.fromFile(f, "utf-8").getLines()).reduceLeft(_ ++ _)
    loadFromIterator(totalIterator)
  }

  def loadFromString(str: String): Unit = {
    val iterator = str.stripMargin.split("\n").toIterator
    loadFromIterator(iterator)
  }

  def loadFromIterator(iterator: Iterator[String]): Unit = {
    val startTime = System.nanoTime()
    // TODO: Option 사용해보자.
    val terms = new mutable.MutableList[Term]()
    iterator.foreach { line =>
      try {
        // FIXME: "," 쉼표 자체는 쌍따옴표로 감싸있음 잘 읽어들이자.
        val l = line.split(",")
        terms += Term(l(0), l(1).toShort, l(2).toShort, l(3).toShort, l.slice(4, l.size).mkString(","))
      } catch {
        case NonFatal(exc) => println(exc)
      }
    }
    println((System.nanoTime() - startTime) / (1000*1000) + " ms")

    build(terms.toIndexedSeq.sortBy(_.surface))
  }

  private def build(sortedTerms: Seq[Term]): Unit = {
    termDict = sortedTerms.toArray
    val startTime = System.nanoTime()
    val surfaceIndexDict = buildSurfaceIndexDict(sortedTerms)

    dictMapper = surfaceIndexDict.map(_._2)

    println((System.nanoTime() - startTime) / (1000*1000) + " ms")

    trie = buildTrie(surfaceIndexDict)
  }

  def buildTrie(dict:Array[(String, Array[Int])]): MapDoubleArray[Int] = {
    var startTime = System.nanoTime()
    val patricia = new MapPatriciaTrie[Int]
    for (idx <- dict.indices) {
      patricia.insert(dict(idx)._1, idx)
    }
    println((System.nanoTime() - startTime) / (1000*1000) + " ms")

    startTime = System.nanoTime()
    val result = new MapDoubleArray(patricia)
    println((System.nanoTime() - startTime) / (1000*1000) + " ms")
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

  def appendBuild(iterable: Iterable[String]): Unit = {
    iterable.foreach(println)

  }

  def prefixSearch(keyword: String): Seq[Term] = {
    val indexedLexiconDictPositions = ListBuffer[Int]()
    val iter = trie.commonPrefixSearchEntries(keyword).iterator()
    while (iter.hasNext) {
      indexedLexiconDictPositions += iter.next().getValue
    }

    indexedLexiconDictPositions.flatMap(mapPos => dictMapper(mapPos)).
      map(termPos => termDict(termPos))
  }

  def save(termDictPath: String = LexiconDict.termDictResourceFile,
           dictMapperPath: String = LexiconDict.dictMapperResourceFile,
           triePath: String = LexiconDict.trieResourceFile): Unit = {

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
    val termDictStream = getClass.getResourceAsStream(LexiconDict.termDictResourceFile)
    val dictMapperStream = getClass.getResourceAsStream(LexiconDict.dictMapperResourceFile)
    val trieStream = getClass.getResourceAsStream(LexiconDict.trieResourceFile)

    load(termDictStream, dictMapperStream, trieStream)
    this
  }

  def load(termDictPath: String = LexiconDict.termDictResourceFile,
           dictMapperPath: String = LexiconDict.dictMapperResourceFile,
           lexiconTriePath: String = LexiconDict.trieResourceFile): Unit = {
    val termDictStream = new FileInputStream(termDictPath)
    val dictMapperStream = new FileInputStream(dictMapperPath)
    val trieStream = new FileInputStream(lexiconTriePath)
    load(termDictStream, dictMapperStream, trieStream)
  }

  private def load(termDictStream: InputStream,
                   dictMapperStream: InputStream,
                   trieStream: InputStream): Unit = {
    var startTime = System.nanoTime()
    val termDictIn = new ObjectInputStream(new BufferedInputStream(termDictStream, 16*1024))
    termDict = termDictIn.readObject().asInstanceOf[Array[Term]]
    termDictIn.close()
    println((System.nanoTime() - startTime) / (1000*1000) + " ms")

    startTime = System.nanoTime()
    val dictMapperIn = new ObjectInputStream(new BufferedInputStream(dictMapperStream, 16*1024))
    dictMapper = dictMapperIn.readObject().asInstanceOf[Array[Array[Int]]]
    dictMapperIn.close()
    println((System.nanoTime() - startTime) / (1000*1000) + " ms")


    startTime = System.nanoTime()
    val TrieIn = new ObjectInputStream(new BufferedInputStream(trieStream, 16*1024))
    trie = TrieIn.readObject().asInstanceOf[MapDoubleArray[Int]]
    TrieIn.close()
    println((System.nanoTime() - startTime) / (1000*1000) + " ms")
  }
}
