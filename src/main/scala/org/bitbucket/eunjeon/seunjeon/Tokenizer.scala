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

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

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
    val charsets = CharDef.splitCharSet(text)
    buildLattice(charsets)
  }

  private def buildLattice(charsets: Seq[CharSet]): Lattice = {
    val charsetsLength = charsets.foldLeft(0)(_ + _.str.length)
    val lattice = new Lattice(charsetsLength, connectionCostDict)

    var charsetOffset = 0
    charsets.foreach { charset: CharSet =>
      addTerms(lattice, charsetOffset, charset)
      charsetOffset += charset.str.length
    }
    lattice
  }

  def addTerms(lattice: Lattice, charsetOffset: Int, charset: CharSet): Unit = {
    val searchedTerms = searchTerms(charset)
    searchedTerms.foreach {
      case (term, start, end) => lattice.add(term, charsetOffset+start, charsetOffset+end)
    }
    val uniqueTerms = searchedTerms.groupBy(t => (t._2, t._3)).map(_._2.head)
    val unknownTerms = findUnknownTerms(charset, uniqueTerms)
    unknownTerms.foreach {
      case (term, start, end) => lattice.add(term, charsetOffset+start, charsetOffset+end)
    }
  }

  def findUnknownTerms(charset: CharSet, searchedTerms: Iterable[(Term, Int, Int)])
  : Seq[(Term, Int, Int)] = {
    // TODO: searchedTerms에 이미 들어 있는 것은 빼자.
    val unknownTerms = new ListBuffer[(Term, Int, Int)]()
    searchedTerms.foreach { case (term, start, end) =>
      if (start > 0) {
        val unknownTerm = Term.createUnknownTerm(charset.str.substring(0, start), charset.term)
        unknownTerms.append((unknownTerm, 0, start-1))
      }
      if (end < charset.str.length-1) {
        val tailUnknownTerm = Term.createUnknownTerm(charset.str.substring(end + 1), charset.term)
        unknownTerms.append((tailUnknownTerm, end+1, charset.str.length-1))
      }
    }

    if (searchedTerms.isEmpty) {
      val unknownTerm = Term.createUnknownTerm(charset.str, charset.term)
      unknownTerms.append((unknownTerm, 0, charset.str.length-1))
    }
    unknownTerms
  }

  def addTermsAndUnKnownTerms(lattice: Lattice, charsetOffset: Int, charset: CharSet, searchedTerms: ListBuffer[(Term, Int, Int)]): Unit = {
    searchedTerms.foreach { case (term, start, end) =>
      if (start > 0) {
        val headUnknownTerm = Term.createUnknownTerm(charset.str.substring(0, start - 1), charset.term)
        lattice.add(headUnknownTerm, charsetOffset, charsetOffset + start - 1)
      }
      lattice.add(term, charsetOffset + start, charsetOffset + end)
      if (end < charset.str.length) {
        val tailUnknownTerm = Term.createUnknownTerm(charset.str.substring(end + 1), charset.term)
        lattice.add(tailUnknownTerm, charsetOffset + end + 1, charsetOffset + charset.str.length)
      }
    }

    if (searchedTerms.isEmpty) {
      lattice.add(Term.createUnknownTerm(charset.str, charset.term), charsetOffset, charsetOffset + charset.str.length - 1)
    }
  }

  def searchTerms(charset: CharSet): ListBuffer[(Term, Int, Int)] = {
    var searchedTerms = new ListBuffer[(Term, Int, Int)]() // term, start, end,
    for (textIdx <- 0 until charset.str.length) {
      val termOffset = textIdx
      val suffixSurface = charset.str.substring(textIdx)
      // 자바(1)/자바(2), 자바스크립트
      val suffixSearchedTerms = lexiconDict.prefixSearch(suffixSurface)
      suffixSearchedTerms.foreach(term =>
        searchedTerms += ((term, termOffset, termOffset + term.surface.length - 1))
      )
    }
    searchedTerms
  }

  //def searchSurface(charset: CharSet):
}

