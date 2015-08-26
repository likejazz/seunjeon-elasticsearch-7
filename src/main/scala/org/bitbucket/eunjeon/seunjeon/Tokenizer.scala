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

  private def buildLattice(text: String): Lattice = {
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

  private def addTerms(lattice: Lattice, charsetOffset: Int, charset: CharSet): Unit = {
    val knownTerms:mutable.Set[LatticeNode] = new mutable.HashSet[LatticeNode]
    val unknownTerms:mutable.Set[LatticeNode] = new mutable.HashSet[LatticeNode]
    for (idx <- 0 until charset.str.length) {
      val termOffset = idx
      val suffixSurface = charset.str.substring(idx)
      // TODO: 인자가 너무 많음. 리팩토링 필요함.
      addKnownTerms(knownTerms, charsetOffset, termOffset, suffixSurface, charset)
      add1NLengthTerms(unknownTerms, knownTerms, charsetOffset, termOffset, suffixSurface, charset)
    }
    addGroupTerm(unknownTerms, knownTerms, charsetOffset, charset)
    lattice.addAll(knownTerms)
    lattice.addAll(unknownTerms)
  }

  private def addKnownTerms(knownTerms:mutable.Set[LatticeNode],
                    charsetOffset: Int,
                    termOffset: Int,
                    suffixSurface: String,
                    charset:CharSet): Unit = {
    val suffixSearchedTerms = lexiconDict.prefixSearch(suffixSurface)
    suffixSearchedTerms.foreach(term =>
      knownTerms += LatticeNode(term, charsetOffset + termOffset, charsetOffset + termOffset + term.surface.length - 1)
    )
  }

  private def add1NLengthTerms(unknownTerms:mutable.Set[LatticeNode],
                       knownTerms:mutable.Set[LatticeNode],
                       charsetOffset: Int,
                       termOffset: Int,
                       suffixSurface: String,
                       charset:CharSet): Unit = {
    if (charset.category.group && charset.category.length == 0) {
      return
    }
    var categoryLength = if (suffixSurface.length < charset.category.length) suffixSurface.length else charset.category.length
    if (categoryLength == 0) {
      categoryLength = 1
    }
    for (unknownIdx <- 1 to categoryLength) {
      val unknownTerm = Term.createUnknownTerm(charset.str.substring(termOffset, termOffset + unknownIdx), charset.term)
      val newLatticeNode = LatticeNode(unknownTerm, charsetOffset + termOffset, charsetOffset + termOffset + unknownTerm.surface.length - 1)
      addUnknownLatticeNode(unknownTerms, knownTerms, charset, newLatticeNode)
    }
  }

  private def addGroupTerm(unknownTerms:mutable.Set[LatticeNode],
                   knownTerms:mutable.Set[LatticeNode],
                   charsetOffset: Int,
                   charset: CharSet): Unit = {
    if (charset.category.group) {
      val fullLengthTerm = Term.createUnknownTerm(charset.str, charset.term)
      val newLatticeNode = LatticeNode(fullLengthTerm, charsetOffset, charsetOffset + fullLengthTerm.surface.length - 1)
      addUnknownLatticeNode(unknownTerms, knownTerms, charset, newLatticeNode)
    }
  }

  private def addUnknownLatticeNode(unknownTerms: mutable.Set[LatticeNode],
                            knownTerms: mutable.Set[LatticeNode],
                            charset: CharSet,
                            newLatticeNode: LatticeNode): Any = {
    if (charset.category.invoke) {
      unknownTerms += newLatticeNode
    } else {
      if (!knownTerms.contains(newLatticeNode)) {
        unknownTerms += newLatticeNode
      }
    }
  }
}

