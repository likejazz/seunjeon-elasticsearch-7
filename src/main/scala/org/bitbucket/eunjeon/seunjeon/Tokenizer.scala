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

import scala.collection.mutable.ListBuffer

class Tokenizer (lexiconDict: LexiconDict = null,
                 connectionCostDict: ConnectionCostDict = null) {
  var userDict:LexiconDict = null

  def setUserDict(dict:LexiconDict): Unit = {
    userDict = dict
  }

  // TODO: 꼭 리팩토링하자
  def parseText(input:String): Seq[LNode] = {
    // TODO: 성능 향상을 위해 intern 잘 활용하도록 고민해보자.
    val text = input.intern()
    text.intern()
    text.split("\n").flatMap(buildLattice(_).getBestPath)
  }

  private def buildLattice(text: String): Lattice = {
    val charsets = CharDef.splitCharSet(text)
    val charsetsLength = charsets.foldLeft(0)(_ + _.str.length)
    Lattice(charsetsLength, connectionCostDict).
      addAll(getKnownTerms(text)).
      addAll(getUnknownTerms(charsets)).
      removeSpace()
  }

  private def getUnknownTerms(charsets: Seq[CharSet]): Seq[LNode] = {
    // TODO: functional 하게 바꾸고 싶음.
    val unknownTerms = new ListBuffer[LNode]
    var charsetOffset = 0
    charsets.foreach { charset: CharSet =>
      unknownTerms ++= getUnknownTerms(charsetOffset, charset)
      charsetOffset += charset.str.length
    }
    unknownTerms
  }

  private def getKnownTerms(text:String): Seq[LNode] = {
    val knownTerms = new ListBuffer[LNode]
    for (idx <- 0 until text.length) {
      knownTerms ++= getKnownTerms(0, idx, text.substring(idx))
    }
    knownTerms
  }

  private def getUnknownTerms(charsetOffset: Int, charset: CharSet): Seq[LNode] = {
    // TODO: 성능이 떨어질까봐 immutable 로 못하겠음...
    val unknownTerms = new ListBuffer[LNode]
    for (idx <- 0 until charset.str.length) {
      val termOffset = idx
      val suffixSurface = charset.str.substring(idx)
      unknownTerms ++= get1NLengthTerms(charsetOffset, termOffset, suffixSurface, charset)
    }

    if (charset.category.group) {
      unknownTerms += getGroupTermNode(charsetOffset, charset)
    }
    unknownTerms
  }

  private def getKnownTerms(charsetOffset: Int,
                            termOffset: Int,
                            suffixSurface: String): Seq[LNode] = {
    var searchedTerms = lexiconDict.commonPrefixSearch(suffixSurface)
    if (userDict != null) {
      searchedTerms ++= userDict.commonPrefixSearch(suffixSurface)
    }
    searchedTerms.map(term =>
      LNode(term, charsetOffset + termOffset, charsetOffset + termOffset + term.surface.length - 1)
    )
  }

  private def get1NLengthTerms(charsetOffset: Int,
                               termOffset: Int,
                               suffixSurface: String,
                               charset:CharSet): Seq[LNode] = {
    if (charset.category.group && charset.category.length == 0) {
      return Seq.empty[LNode]
    }
    var categoryLength = if (suffixSurface.length < charset.category.length) suffixSurface.length else charset.category.length
    if (categoryLength == 0) {
      categoryLength = 1
    }

    (1 to categoryLength).map { unknownIdx =>
      val unknownTerm = Morpheme.createUnknownMorpheme(charset.str.substring(termOffset, termOffset + unknownIdx),
                                               charset.term)
      LNode(unknownTerm, charsetOffset + termOffset, charsetOffset + termOffset + unknownTerm.surface.length - 1)
    }
  }

  private def getGroupTermNode(charsetOffset: Int, charset: CharSet): LNode = {
    val fullLengthTerm = Morpheme.createUnknownMorpheme(charset.str, charset.term)
    LNode(fullLengthTerm, charsetOffset, charsetOffset + fullLengthTerm.surface.length - 1)
  }
}

