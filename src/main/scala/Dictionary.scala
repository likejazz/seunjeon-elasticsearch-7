import dict.DoubleArrayTrie
import scala.collection.JavaConversions._
import scala.collection.mutable

import scala.io.Source

case class Term(surface:String,
                leftId:Short,
                rightId:Short,
                cost:Int,
                feature:Seq[String]) {
}

object Term {
  def createUnknownTerm(surface:String): Term = {
    new Term(surface, -1, -1, 9999*surface.length, Seq[String]())
  }
}

class Dictionary {
  var indexedLexiconDict: IndexedSeq[(String, Seq[Term])] = null
  var trie: DoubleArrayTrie = new DoubleArrayTrie()
  // TODO: cost dictionary
  var costDict: Array[Array[Int]] = null//Array.ofDim[Short](1, 2)
  
  def loadDict(): Unit = {
    loadDict(
      Source.fromFile(Dictionary.LEXICON_DICT_PATH, "utf-8").getLines(),
      Source.fromFile(Dictionary.COST_DICT_PATH, "utf-8").getLines()
    )
  }

  def loadDict(lexiconDict: Iterator[String],
               costDict: Iterator[String]): Unit = {
    loadLexiconDict(lexiconDict)
    loadCostDict(costDict)
    this
  }

  def loadLexiconDict(lexiconDictIter: Iterator[String]): Unit = {
    val terms = lexiconDictIter.map(l => l.split(",")).
      map(l => new Term(l(0), l(1).toShort, l(2).toShort, l(3).toShort, l.slice(4, l.size-1))).toIndexedSeq
    val groupedTerms = terms.groupBy(t => t.surface).toIndexedSeq
    val sortedGroupTerms = groupedTerms.sortBy(t => t._1)

    indexedLexiconDict = sortedGroupTerms

    // 주의: !!! seq를 정렬해서 넘겨줘야 trie가 동작함.
    trie.build(sortedGroupTerms.map(t => t._1))
  }

  // TODO: 로딩시간이 많이 걸림. 직렬화할필요가 있어보임.. lexicon사전도 마찬가지..
  def loadCostDict(costDictIter: Iterator[String]): Unit = {
    val costs_file = costDictIter.toSeq
    val sizes = costs_file.head.split(' ').map(v => v.toShort)
    costDict = Array.ofDim[Int](sizes(0), sizes(1))
    val costs = costs_file.tail.map(_.split(' ').map(_.toShort)).
      foreach(v => costDict(v(0))(v(1)) = v(2))
  }

  // TODO: 꼭 리팩토링하자
  def parseText(text:String): Seq[Term] = {
    val lattice = buildLattice(text.split(" "))
    lattice.getBestPath
  }

  def buildLattice(eojeols: Array[String]): Lattice = {
    val lattice = new Lattice(eojeols.foldLeft(0)(_ + _.length), costDict)
    var eojeolOffset = 0
    eojeols.foreach { eojeol: String =>
      for (textIdx <- 0 to eojeol.length) {
        val termOffset = eojeolOffset + textIdx
        val suffixSurface = eojeol.substring(textIdx)
        // 자바(1)/자바(2), 자바스크립트
        val indexedLexiconDictPositions = trie.commonPrefixSearch(suffixSurface, 0, 0, 0)
        val searchedTerms = indexedLexiconDictPositions.flatMap { indexLexiconDictPos =>
          indexedLexiconDict.get(indexLexiconDictPos)._2
        }
        addTermsToLattice(lattice, searchedTerms, termOffset)
        val termLengthSet = searchedTerms.map(term => term.surface.length).toSet

        // TODO: insert unknown-word to lattice
        // 자, 자바스, 자바스크, 자바스크립
        val unkownTerms = (1 to suffixSurface.length).toSet.diff(termLengthSet).
          map( i => Term.createUnknownTerm(suffixSurface.substring(0, i)))
        unkownTerms.foreach(unknownTerm =>
          lattice.add(unknownTerm, termOffset, termOffset + unknownTerm.surface.length - 1))
      }
      eojeolOffset += eojeol.length
    }
    lattice
  }

  def addTermsToLattice(lattice: Lattice, terms: Seq[Term], termOffset: Int): Unit = {
    terms.foreach { term =>
      lattice.add(term, termOffset, termOffset + term.surface.length - 1)
    }
  }
}

// csv 사전 로딩
object Dictionary {
  val LEXICON_DICT_PATH = "/home/parallels/Downloads/mecab-ko-dic-1.6.1-20140814/NNG.csv"
  val COST_DICT_PATH = "/home/parallels/Downloads/mecab-ko-dic-1.6.1-20140814/matrix.def"
  //val path = "/home/parallels/Downloads/test.csv"
}
