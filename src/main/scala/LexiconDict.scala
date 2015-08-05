import java.io._

import com.google.common.collect.ImmutableList
import dict.DoubleArrayTrie

import scala.collection.JavaConversions._
import scala.io.Source
import scala.util.matching.Regex

// TODO: serialize
class LexiconDict {
  var surfaceIndexDict: ImmutableList[(String, ImmutableList[Term])] = null
  var trie: DoubleArrayTrie = null

  def loadFromPath(dir: String): Unit = {
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
    // TODO: exeption
    val terms: Iterator[Term] = iterator.map {line: String =>
      val l = line.split(",")
      new Term(l(0), l(1).toShort, l(2).toShort, l(3).toShort, l.slice(4, l.size - 1).mkString(","))
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

    trie = new DoubleArrayTrie
    trie.build(surfaceIndexDict.map(t => t._1))
  }

  def prefixSearch(keyword: String): Seq[Term] = {
    val indexedLexiconDictPositions = trie.commonPrefixSearch(keyword, 0, 0, 0)
    indexedLexiconDictPositions.flatMap { indexLexiconDictPos =>
      surfaceIndexDict.get(indexLexiconDictPos)._2
    }
  }

  def save(): Unit = {
    // Term 에서 surface 를 빼면 serialize deserialze하는데 더 빠를 것 같음.
    val store = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("store.dat"), 1024*16))
    store.writeObject(surfaceIndexDict)
    store.close
    trie.save("trie.dat")
  }

  def open(): Unit = {
    val in = new ObjectInputStream(new BufferedInputStream(new FileInputStream("store.dat"), 1024*16))
    surfaceIndexDict = in.readObject().asInstanceOf[ImmutableList[(String, ImmutableList[Term])]]
    in.close()

    trie = new DoubleArrayTrie
    trie.open("trie.dat")
  }
}
