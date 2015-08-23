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

import java.util

import scala.collection.mutable
import scala.io.Source



// TODO: unk.def 파일에서 좌/우/비용 찾아서 넣어주자.
case class CharSet(str: String, category: Term)

// TODO
object UnkDef {
  val terms = buildUnk
  var defaultTerm: Term = null

  def buildUnk: mutable.Map[String, Term] = {
    val terms = mutable.Map[String, Term]()
    Source.fromFile(DicBuilder.RESOURCE_PATH + "/unk.def", "utf-8").getLines().foreach { line =>
      val l = line.split(",")
      if (l(0) == "DEFAULT") {
        defaultTerm =
          Term(l(0), l(1).toShort, l(2).toShort, l(3).toShort, l.slice(4, l.size).mkString(","))
      } else {
        terms(l(0)) = Term(l(0), l(1).toShort, l(2).toShort, l(3).toShort, l.slice(4, l.size).mkString(","))
      }
    }
    terms
  }

  def apply(name: String): Option[Term] = {
    terms.get(name)
  }
}

/* TODO: 해석해주자...
DEFAULT         0 1 0  # DEFAULT is a mandatory category!
SPACE           0 1 0
HANJA           0 0 1
KANJI           0 0 2
SYMBOL          1 1 0
NUMERIC         1 1 0
ALPHA           1 1 0
HANGUL          0 1 2 # Korean
HIRAGANA        1 1 0
KATAKANA        1 1 0
HANJANUMERIC    1 1 0
GREEK           1 1 0
CYRILLIC        1 1 0
 */

object CharDef {
  val charFinder = loadChar

  def loadChar: util.TreeMap[Char, Term] = {
    val charMap = new util.TreeMap[Char, Term]()
    Source.fromFile(DicBuilder.RESOURCE_PATH + "/char.def", "utf-8").getLines().foreach { line =>
      val l = line.split("\\s+")
      if (l(0).startsWith("0x")) {
        val charRange = l(0).split("\\.\\.")
        val name = l(1)
        charRange.foreach { range =>
          val term = UnkDef(name).orNull
          if (term != null) {
            charMap.put(Integer.parseInt(range.substring(2), 16).toChar, term)
          }
        }
      }
    }
    charMap
  }

  def splitCharSet(text: String): Seq[CharSet] = {
    val result = new mutable.ListBuffer[CharSet]
    if (text.length == 0) {
      return result;
    }
    var start = 0
    var curCategory: Term = null
    text.view.zipWithIndex.foreach { case (ch, idx) =>
      val charSet: Term = getCharSet(ch)
      if (charSet != curCategory) {
        // first loop
        if (curCategory == null) {
        } else {
          result.append(CharSet(text.substring(start, idx), curCategory))
          start = idx
        }
        curCategory = charSet
      }
    }
    result.append(CharSet(text.substring(start, text.length), curCategory))
    // TODO: pos id 바꾸자 string 비교는 느릴것같음.
    // remove space term
    result.filterNot(_.category.surface == "SPACE")
  }

  private def getCharSet(ch: Char): Term = {
    val floor = charFinder.floorEntry(ch)
    val ceiling = charFinder.ceilingEntry(ch)
    if (floor == null || ceiling == null) {
      UnkDef.defaultTerm
    } else if (floor.getValue == ceiling.getValue) {
      floor.getValue
    } else {
      UnkDef.defaultTerm
    }
  }
}
