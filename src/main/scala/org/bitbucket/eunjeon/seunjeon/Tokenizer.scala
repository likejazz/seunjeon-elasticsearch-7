package org.bitbucket.eunjeon.seunjeon

import scala.collection.mutable

class Tokenizer (lexiconDict: LexiconDict = null,
                 connectionCostDict: ConnectionCostDict = null) {

  // TODO: 꼭 리팩토링하자
  def parseText(input:String): Seq[Term] = {
    // TODO: 성능 향상을 위해 intern 잘 활용하도록 고민해보자.
    val text = input.intern()
    text.intern()
    var result: Seq[Term] = new mutable.ListBuffer()
    text.split("\n").foreach{line =>
      val lattice = buildLattice(line)
      result ++= lattice.getBestPath
    }
    result
  }

  def buildLattice(text: String): Lattice = {
    val charsets = CharSet.splitCharSet(text)
    val charsetsLength = charsets.foldLeft(0)(_ + _.str.length)
    val lattice = new Lattice(charsetsLength, connectionCostDict)
    buildLattice(charsets, lattice)
    lattice
  }

  private def buildLattice(eojeols: Seq[CharSetCategory], lattice: Lattice): Unit = {
    var eojeolOffset = 0
    eojeols.foreach { eojeol: CharSetCategory =>
      for (textIdx <- 0 to eojeol.str.length) {
        val termOffset = eojeolOffset + textIdx
        val suffixSurface = eojeol.str.substring(textIdx)
        // 자바(1)/자바(2), 자바스크립트
        val searchedTerms = lexiconDict.prefixSearch(suffixSurface)
        addTermsToLattice(lattice, searchedTerms, termOffset)
        val termLengthSet = searchedTerms.map(term => term.surface.length).toSet

        // TODO: insert unknown-word to lattice
        // 자, 자바스, 자바스크, 자바스크립
        val unkownTerms = (1 to suffixSurface.length).toSet.diff(termLengthSet).
          map( i => Term.createUnknownTerm(suffixSurface.substring(0, i), eojeol.category.toString))
        unkownTerms.foreach(unknownTerm =>
          lattice.add(unknownTerm, termOffset, termOffset + unknownTerm.surface.length - 1))
      }
      eojeolOffset += eojeol.str.length
    }
  }

  def addTermsToLattice(lattice: Lattice, terms: Seq[Term], termOffset: Int): Unit = {
    terms.foreach { term =>
      lattice.add(term, termOffset, termOffset + term.surface.length - 1)
    }
  }
}

