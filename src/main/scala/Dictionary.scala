import dict.DoubleArrayTrie
import scala.collection.JavaConversions._

import scala.io.Source

case class Term(surface:String,
                leftId:Short,
                rightId:Short,
                cost:Short,
                feature:Seq[String])

class Dictionary {
  var indexedLexiconDict: IndexedSeq[(String, Seq[Term])] = null
  var trie: DoubleArrayTrie = new DoubleArrayTrie()
  // TODO: cost dictionary
  var costDict: Array[Array[Short]] = null//Array.ofDim[Short](1, 2)
  
  def loadDict(): Unit = {
    loadLexiconDict()
    loadCostDict()
  }

  def loadLexiconDict(): Unit = {
    val terms = Source.fromFile(Dictionary.LEXICON_DICT_PATH, "utf-8").getLines().
      map(l => l.split(",")).
      map(l => new Term(l(0), l(1).toShort, l(2).toShort, l(3).toShort, l.slice(4, l.size-1))).toIndexedSeq
    val groupedTerms = terms.groupBy(t => t.surface).toIndexedSeq
    val sortedGroupTerms = groupedTerms.sortBy(t => t._1)

    indexedLexiconDict = sortedGroupTerms

    // 주의: !!! seq를 정렬해서 넘겨줘야 trie가 동작함.
    trie.build(sortedGroupTerms.map(t => t._1))
  }

  // TODO: 로딩시간이 많이 걸림. 직렬화할필요가 있어보임.. lexicon사전도 마찬가지..
  def loadCostDict(): Unit = {
    val costs_file = Source.fromFile(Dictionary.COST_DICT_PATH, "utf-8").getLines().toSeq
    val sizes = costs_file.head.split(' ').map(v => v.toShort)
    costDict = Array.ofDim[Short](sizes(0), sizes(1))
    val costs = costs_file.tail.map(_.split(' ').map(_.toShort)).
      foreach(v => costDict(v(0))(v(1)) = v(2))
  }

  def parseText(text:String): Seq[Term] = {
    val lattice = new Lattice(text.length, costDict)
    for (textIdx <- 0 to text.length) {
      trie.commonPrefixSearch(text, textIdx, 0, 0).foreach{ i =>
        val terms:(String, Seq[Term]) = indexedLexiconDict.get(i)
        terms._2.foreach(term => lattice.add(term, textIdx, textIdx + term.surface.length-1))
      }
    }
    lattice.getBestPath
  }
}

object Dictionary {
  val LEXICON_DICT_PATH = "/home/parallels/Downloads/mecab-ko-dic-1.6.1-20140814/NNG.csv"
  val COST_DICT_PATH = "/home/parallels/Downloads/mecab-ko-dic-1.6.1-20140814/matrix.def"
  //val path = "/home/parallels/Downloads/test.csv"

}
