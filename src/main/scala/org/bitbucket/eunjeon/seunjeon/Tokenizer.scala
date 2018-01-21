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

import java.lang

import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConverters._

class Tokenizer(lexiconDict: LexiconDict, connectionCostDict: ConnectionCostDict, compress: Boolean) {
  // TODO: atomic 해야 하지만.. 아직은.. 어떻게 해야할지 고민이 필요함.
  private[this] var userDict:LexiconDict = null
  private[this] var maxUnkLength = 8

  def setUserDict(dict:LexiconDict) = { userDict = dict }
  def setMaxUnkLength(length:Int) = { maxUnkLength = length }

  def parseText(input:String, dePreAnalysis:Boolean): Iterable[Paragraph] = {
    val text = input.intern()
    var offset = 0
    val lineSeparator = System.lineSeparator()
    val bestPath: Iterable[Paragraph] = text.split(lineSeparator).toStream.map { str =>
        val path = buildLattice(str).getBestPath(offset)
        offset += str.length + lineSeparator.length
        path
      }.map(x => Paragraph(removeHeadLast(x)))

    if (dePreAnalysis) bestPath.map(x => Paragraph(x.nodes.flatMap(LNode.dePreAnalysis))) // flatMap(LNode.dePreAnalysis)//.flatMap(LNode.deInflect)
    else bestPath
  }

  def parseTextJava(input:String, dePreAnalysis:Boolean): lang.Iterable[Paragraph] =
    parseText(input, dePreAnalysis).asJava

  private def removeHeadLast(nodes:Seq[LNode]): Seq[LNode] = {
    nodes.slice(1, nodes.length - 1)
  }

  private def buildLattice(text: String): Lattice = {
//    val charsets = CharDef.splitCharSet(text)
    Lattice(text, connectionCostDict).
      addAll(getKnownTerms(text)).
      addAll(getUnknownTerms(text)).
      build()
  }

  private def getUnknownTerms(text: String): Seq[LNode] = {
    val charsets = CharSetDef.splitCharSet(text)
    // TODO: functional 하게 바꾸고 싶음.
    val unknownTerms = new ArrayBuffer[LNode]
    var charsetOffset = 0
    charsets.foreach { charset: CharSet =>
      unknownTerms ++= getUnknownTerms(charsetOffset, charset)
      charsetOffset += charset.str.length
    }
    unknownTerms
  }

  private def getKnownTerms(text:String): Seq[LNode] = {
    // TODO: space 들어간 단어는 잘 처리하자.
    val knownTerms = new ArrayBuffer[LNode]
    for (idx <- 0 until text.length) {
      knownTerms ++= getKnownTerms(0, idx, text.substring(idx))
    }
    knownTerms
  }

  private def getUnknownTerms(charsetOffset: Int, charset: CharSet): Seq[LNode] = {
    // TODO: 성능이 떨어질까봐 immutable 로 못하겠음...
    val unknownTerms = new ArrayBuffer[LNode]
    for (idx <- 0 until charset.str.length) {
      val termOffset = idx
      val suffixSurface = charset.str.substring(idx)
      unknownTerms ++= get1NLengthTerms(charsetOffset, termOffset, suffixSurface, charset)
    }


    if (charset.category.length == 0) {
      unknownTerms += getGroupTermNode(charsetOffset, charset)
    } else {
      if (charset.category.group && charset.str.length <= maxUnkLength) {
        unknownTerms += getGroupTermNode(charsetOffset, charset)
      }
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
      LNode(term, charsetOffset + termOffset, charsetOffset + termOffset + term.getSurface.length)
    )
  }

  private[this] def get1NLengthTerms(charsetOffset: Int,
                               termOffset: Int,
                               suffixSurface: String,
                               charset:CharSet): Seq[LNode] = {
    if (charset.category.group && charset.category.length == 0) {
      return Seq.empty[LNode]
    }
    var categoryLength = math.min(math.min(suffixSurface.length, charset.category.length), maxUnkLength)

    if (categoryLength == 0) {
      categoryLength = 1
    }

    // 성능때문에 while 사용
    var unknownIdx = 1
    val nodes = new Array[LNode](categoryLength)
    while (unknownIdx <= categoryLength) {
      val tmp: Morpheme = UnkMorpheme(charset.str.substring(termOffset, termOffset + unknownIdx), charset.morpheme)
      val unknownTerm = if (compress) new CompressedMorpheme(tmp) else tmp
      nodes(unknownIdx-1) = LNode(unknownTerm, charsetOffset + termOffset, charsetOffset + termOffset + unknownTerm.getSurface.length)
      unknownIdx += 1
    }
    nodes
  }

  private def getGroupTermNode(charsetOffset: Int, charset: CharSet): LNode = {
    val fullLengthTerm = UnkMorpheme.apply(charset.str, charset.morpheme)
    LNode(fullLengthTerm, charsetOffset, charsetOffset + fullLengthTerm.getSurface.length)
  }
}

