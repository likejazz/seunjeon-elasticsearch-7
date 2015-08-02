import java.io._
import scala.collection.JavaConversions._

import dict.DoubleArrayTrie

import scala.io.Source
import scala.util.matching.Regex

// TODO: serialize
class LexiconDict {
  var indexedDict: IndexedSeq[(String, Seq[Term])] = null
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
      new Term(l(0), l(1).toShort, l(2).toShort, l(3).toShort, l.slice(4, l.size - 1))
    }
    build(terms.toIndexedSeq)
  }

  private def build(terms: Seq[Term]): Unit = {
    indexedDict = terms.groupBy(t => t.surface).toIndexedSeq.sortBy(t => t._1)
    trie = new DoubleArrayTrie
    trie.build(indexedDict.map(t => t._1))
  }

  def prefixSearch(keyword: String): Seq[Term] = {
    val indexedLexiconDictPositions = trie.commonPrefixSearch(keyword, 0, 0, 0)
    indexedLexiconDictPositions.flatMap { indexLexiconDictPos =>
      indexedDict.get(indexLexiconDictPos)._2
    }
  }

  def save(): Unit = {
    val store = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("store.dat"), 16384))
    store.writeObject(indexedDict)
    store.close
    trie.save("trie.dat")
  }

  def open(): Unit = {
    val in = new ObjectInputStream(new BufferedInputStream(new FileInputStream("store.dat"), 16384))
    indexedDict = in.readObject().asInstanceOf[IndexedSeq[(String, Seq[Term])]]
    in.close()

    trie = new DoubleArrayTrie
    trie.open("trie.dat")
  }
}
