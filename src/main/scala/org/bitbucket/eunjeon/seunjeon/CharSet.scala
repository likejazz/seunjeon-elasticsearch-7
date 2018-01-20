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
import scala.io.Source
import scala.collection.mutable.ArrayBuffer


case class CharSet(str: String, rlength: Int, category: Category, morpheme: Morpheme)
case class Category(invoke:Boolean, group:Boolean, length:Int)


object CharSetDef {

  val (
    charset: Array[Byte],
    cateMorphemeIndex: Array[(Category, Morpheme)]) = loadResource

  def loadResource: (Array[Byte], Array[(Category, Morpheme)]) = {
    val lines: Seq[String] =
    Source.fromInputStream(classOf[CharSet].getResourceAsStream(DictBuilder.CHAR_DEF_FILENAME), "UTF-8").getLines().
      filterNot(line => line.startsWith("#") || line.replaceAll("\\s", "").isEmpty).toSeq

    val (rangeLines: Seq[String], categoryLines: Seq[String]) = lines.partition(_.startsWith("0x"))

    val categories: Map[String, Category] = categoryLines.map(parseCategory).toMap

    val parsedRanges: Seq[(Char, Char, (Category, Morpheme))] =
      rangeLines.
        map(parseCharset).
        map(x => (x._1, x._2, (categories(x._3), UnkDef(x._3).get)))

    val cateMorphemes: Array[(Category, Morpheme)] = buildCategoryMorphemes(parsedRanges)
    val charsetIndex: ArrayBuffer[Byte] = buildCharset(parsedRanges, cateMorphemes)

    (charsetIndex.toArray, cateMorphemes)
  }

  private def buildCategoryMorphemes(parsedRanges: Seq[(Char, Char, (Category, Morpheme))]) = {
    val tmp = parsedRanges.map(_._3).distinct.partition(_._2.getSurface == "DEFAULT")
    (tmp._1 ++ tmp._2).toArray
  }

  private def buildCharset(parsedLines: Seq[(Char, Char, (Category, Morpheme))], cateMorphemes: Array[(Category, Morpheme)]) = {
    val cateMorphemeIndex: Map[(Category, Morpheme), Byte] = cateMorphemes.zipWithIndex.map(x => (x._1, x._2.toByte)).toMap
    val charsetIndex = ArrayBuffer.fill[Byte](Char.MaxValue+1)(0)
    parsedLines.foreach { parsedLine =>
      val begin = parsedLine._1
      val finish = parsedLine._2
      val cateMorpheme = parsedLine._3
      for (idx <- begin to finish) {
        charsetIndex(idx) = cateMorphemeIndex(cateMorpheme)
      }
    }
    charsetIndex
  }

  private[seunjeon] def parseCharset(line: String): (Char, Char, String) = {
    val l = line.split("\\s+")
    val charRange = l(0).split("\\.\\.")
    val name = l(1)

    def str2Char(hexaDecimal: String) = Integer.parseInt(hexaDecimal, 16).toChar

    (str2Char(charRange.head.substring(2)), str2Char(charRange.last.substring(2)), name)
  }

  private def parseCategory(line: String) = {
    val l = line.split("\\s+")
    val name = l(0)
    val invoke = if (l(1) == "1") true else false
    val group = if (l(2) == "1") true else false
    val length = l(3).toInt
    name -> Category(invoke, group, length)
  }

  def splitCharSet(text: String): Seq[CharSet] = {
    val charsets = new mutable.ArrayBuffer[CharSet](text.length)
    if (text.length == 0) {
      return charsets
    }
    var start = 0
    var curCategoryTerm: (Category, Morpheme) = null
    text.zipWithIndex.foreach { case (ch, idx) =>
      val categoryTerm: (Category, Morpheme) = getCategoryTerm(ch)
      if (categoryTerm != curCategoryTerm) {
        // first loop
        if (curCategoryTerm == null) {
        } else {
          val charsetString = text.substring(start, idx)
          charsets.append(CharSet(charsetString, charsetString.length, curCategoryTerm._1, curCategoryTerm._2))
          start = idx
        }
        curCategoryTerm = categoryTerm
      }
    }
    val charsetString = text.substring(start, text.length)
    charsets.append(CharSet(charsetString, charsetString.length, curCategoryTerm._1, curCategoryTerm._2))

    charsets
  }

  def getCategoryTerm(ch: Char): (Category, Morpheme) = {
    val idx = charset(ch)
    cateMorphemeIndex(idx)
  }
}
