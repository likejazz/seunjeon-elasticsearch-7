
case class Term(surface:String,
                leftId:Short,
                rightId:Short,
                cost:Int,
                feature:Seq[String]) {
}

object Term {
  def createUnknownTerm(surface:String): Term = {
    new Term(surface, -1, -1, 500*surface.length, Seq[String]("UNKNOWN"))
  }
}

class Tagger {
  var lexiconDict: LexiconDict = null
  var connectionCostDict: ConnectionCostDict = null
  
  def setLexiconDict(dict: LexiconDict): Unit = {
    lexiconDict = dict
  }

  def setConnectionCostDict(dict: ConnectionCostDict): Unit = {
    connectionCostDict = dict
  }

  // TODO: 꼭 리팩토링하자
  def parseText(text:String): Seq[Term] = {
    val lattice = buildLattice(text.split(" "))
    lattice.getBestPath
  }

  def buildLattice(eojeols: Array[String]): Lattice = {
    val lattice = new Lattice(eojeols.foldLeft(0)(_ + _.length), connectionCostDict)
    var eojeolOffset = 0
    eojeols.foreach { eojeol: String =>
      for (textIdx <- 0 to eojeol.length) {
        val termOffset = eojeolOffset + textIdx
        val suffixSurface = eojeol.substring(textIdx)
        // 자바(1)/자바(2), 자바스크립트
        val searchedTerms = lexiconDict.prefixSearch(suffixSurface)
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

