package org.bitbucket.eunjeon.seunjeon

import java.io.{File, _}

import com.google.common.collect.ImmutableList
import org.trie4j.doublearray.MapDoubleArray
import org.trie4j.patricia.MapPatriciaTrie

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.util.control.NonFatal
import scala.util.matching.Regex

object Term {
  def createUnknownTerm(surface:String, category: String): Term = {
    new Term(surface, -1, -1, 4000*surface.length, category)
  }
}

case class Term(surface:String,
                leftId:Short,
                rightId:Short,
                cost:Int,
                feature:String) {
}

object LexiconDict {
  val lexiconResourceFile = "/lexicon.dat"
  val lexiconTrieResourceFile = "/lexicon_trie.dat"
}

class LexiconDict {
  var surfaceIndexDict: ImmutableList[(String, ImmutableList[Term])] = null
  var trie: MapDoubleArray[Int] = null

  def loadFromCsvFiles(dir: String): Unit = {
    val r = new Regex(".+[.]csv")
    val files = new File(dir).listFiles.filter(f => r.findFirstIn(f.getName).isDefined)
    val totalIter:Iterator[String] = files.map(f => Source.fromFile(f, "utf-8").getLines()).reduceLeft(_ ++ _)
    loadFromIterator(totalIter)
  }

  def loadFromString(str: String): Unit = {
    val iterator = str.stripMargin.split("\n").toIterator
    loadFromIterator(iterator)
  }

  def loadFromIterator(iterator: Iterator[String]): Unit = {
    // TODO: Option 사용해보자.
    val terms = new mutable.MutableList[Term]()
    iterator.foreach { line =>
      try {
        val l = line.split(",")
        // TODO: 사전에 이상한 문자가 있어서 trie이가 이상하게 돌아가는 듯.. 원인일 찾아봐야 함.
        // 그래서 "흐라" 검색하니 "헬렌켈러"가 나옴.
        terms += Term(l(0), l(1).toShort, l(2).toShort, l(3).toShort, l.slice(4, l.size - 1).mkString(","))
      } catch {
        case NonFatal(exc) => println(exc)
      }
    }
    build(terms.toIndexedSeq)
  }

  private def build(terms: Seq[Term]): Unit = {
    val surfaceIndexDictTemp = terms.groupBy(t => t.surface).toIndexedSeq.sortBy(t => t._1)
    val surfaceIndexDictTemp2 = surfaceIndexDictTemp.map{it =>
      val surface = it._1
      val terms = ImmutableList.copyOf[Term](it._2)
      (surface, terms)
    }
    surfaceIndexDict = ImmutableList.copyOf[(String, ImmutableList[Term])](surfaceIndexDictTemp2)

    val patricia = new MapPatriciaTrie[Int]
    for (idx <- 0 until surfaceIndexDict.size) {
      patricia.insert(surfaceIndexDict.get(idx)._1, idx)
    }
    trie = new MapDoubleArray(patricia)
  }

  def prefixSearch(keyword: String): Seq[Term] = {
    //val indexedLexiconDictPositions = trie.commonPrefixSearch(keyword)
    val indexedLexiconDictPositions = ListBuffer[Int]()
    val iter = trie.commonPrefixSearchEntries(keyword).iterator()
    while (iter.hasNext) {
      indexedLexiconDictPositions += iter.next().getValue
    }

//    val indexedLexiconDictPositions = ListBuffer[Int]()
//    for (idx <- 0 to keyword.length) {
//      val subKeyword = keyword.substring(idx, idx+1)
//      indexedLexiconDictPositions += trie.getValueForExactKey(subKeyword)
//    }
    indexedLexiconDictPositions.flatMap { indexLexiconDictPos =>
      surfaceIndexDict.get(indexLexiconDictPos)._2
    }
  }

  def save(lexiconPath: String = LexiconDict.lexiconResourceFile,
           lexiconTriePath: String = LexiconDict.lexiconTrieResourceFile): Unit = {
    // TODO: Term 에서 surface 를 빼면 serialize deserialze하는데 더 빠를 것 같음.
    val lexiconStore = new ObjectOutputStream(
      new BufferedOutputStream(
        new FileOutputStream(lexiconPath), 1024*16))
    lexiconStore.writeObject(surfaceIndexDict)
    lexiconStore.close()

    val trieStore = new ObjectOutputStream(
      new BufferedOutputStream(
        new FileOutputStream(lexiconTriePath), 1024*16))
    trieStore.writeObject(trie)
    trieStore.close()
  }

  def load(): LexiconDict = {
    val lexiconStream = getClass.getResourceAsStream(LexiconDict.lexiconResourceFile)
    val lexiconTrieStream = getClass.getResourceAsStream(LexiconDict.lexiconTrieResourceFile)

    load(lexiconStream, lexiconTrieStream)
    this
  }

  def load(lexiconPath: String = LexiconDict.lexiconResourceFile,
           lexiconTriePath: String = LexiconDict.lexiconTrieResourceFile): Unit = {
    val lexiconStream = new FileInputStream(lexiconPath)
    val lexiconTrieStream = new FileInputStream(lexiconTriePath)
    load(lexiconStream, lexiconTrieStream)
  }

  private def load(lexiconStream: InputStream, lexiconTrieStream: InputStream): Unit = {
    val lexiconIn = new ObjectInputStream(
      new BufferedInputStream(lexiconStream, 1024*16))
    surfaceIndexDict = lexiconIn.readObject().asInstanceOf[ImmutableList[(String, ImmutableList[Term])]]
    lexiconIn.close()


    val TrieIn = new ObjectInputStream(
      new BufferedInputStream(lexiconTrieStream, 1024*16))
    trie = TrieIn.readObject().asInstanceOf[MapDoubleArray[Int]]
    TrieIn.close()
  }
}
