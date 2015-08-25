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
    for (idx <- 0 until charset.str.length) {
      val termOffset = idx
      val suffixSurface = charset.str.substring(idx)
      val suffixSearchedTerms = lexiconDict.prefixSearch(suffixSurface)
      suffixSearchedTerms.foreach(term =>
        lattice.add(term, charsetOffset+termOffset, charsetOffset+termOffset+term.surface.length-1)
      )

      val categoryLength = if (termOffset+charset.category.length > charset.str.length) charset.category.length-1 else charset.category.length
      for (unknownIdx <- 1 to categoryLength) {
        val unknownTerm = Term.createUnknownTerm(charset.str.substring(termOffset, termOffset+unknownIdx), charset.term)
        lattice.add(unknownTerm, charsetOffset+termOffset, charsetOffset+termOffset+unknownTerm.surface.length-1)
      }
    }
    // TODO: 심각한 성능문제가 있음.
    val fullLengthTerm = Term.createUnknownTerm(charset.str, charset.term)
    lattice.add(fullLengthTerm, charsetOffset, charsetOffset+fullLengthTerm.surface.length-1)
  }
}

