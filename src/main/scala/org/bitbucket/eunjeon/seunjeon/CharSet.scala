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
case class CharSet(str: String, rlength: Int, category: Category, term: Term)

// TODO
object UnkDef {
  // defaultTerm, terms 순서가 중요하다.. refactoring하자.
  var defaultTerm: Term = null
  val terms = buildUnk

  def buildUnk: mutable.Map[String, Term] = {
    val terms = mutable.Map[String, Term]()
    val inputStream = getClass.getResourceAsStream(DictBuilder.UNK_DEF)
    Source.fromInputStream(inputStream).getLines().foreach { line =>
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

case class Category(invoke:Boolean, group:Boolean, length:Int)

// TODO: charset, category 구조가 잘 안잡힌듯.. 교통정리가 필요함.
object CharDef {
  var defaultCategory:Category = null
  val charFinder:util.TreeMap[Char, (Category, Term)] = loadChar

  def loadChar = {
    val categories = mutable.Map[String, Category]()
    val charMap = new util.TreeMap[Char, (Category, Term)]()
    val inputStream = getClass.getResourceAsStream(DictBuilder.CHAR_DEF)
    Source.fromInputStream(inputStream).getLines().
      filterNot(line => line.startsWith("#") || line.length == 0).
      foreach { line =>
        val l = line.split("\\s+")
        // range
        if (l(0).startsWith("0x")) {
          val charRange = l(0).split("\\.\\.")
          val name = l(1)
          charRange.foreach { range =>
            val term = UnkDef(name).orNull
            if (term != null) {
              charMap.put(Integer.parseInt(range.substring(2), 16).toChar, (categories(name), term))
            }
          }
        // category
        } else {
          val l = line.split("\\s+")
          val name = l(0)
          val invoke = if (l(1) == "1") true else false
          val group = if (l(2) == "1") true else false
          val length = l(3).toInt
          if (name == "DEFAULT") {
            defaultCategory = Category(invoke, group, length)
          } else {
            categories(name) = Category(invoke, group, length)
          }
        }
      }
    charMap
  }

  def splitCharSet(text: String): Seq[CharSet] = {
    val result = new mutable.ListBuffer[CharSet]
    if (text.length == 0) {
      return result
    }
    var start = 0
    var curCategoryTerm: (Category, Term) = null
    text.view.zipWithIndex.foreach { case (ch, idx) =>
      val categoryTerm: (Category, Term) = getCategoryTerm(ch)
      if (categoryTerm != curCategoryTerm) {
        // first loop
        if (curCategoryTerm == null) {
        } else {
          val charsetString = text.substring(start, idx)
          result.append(CharSet(charsetString, charsetString.length, curCategoryTerm._1, curCategoryTerm._2))
          start = idx
        }
        curCategoryTerm = categoryTerm
      }
    }
    val charsetString = text.substring(start, text.length)
    result.append(CharSet(charsetString, charsetString.length, curCategoryTerm._1, curCategoryTerm._2))

    result
  }

  private def getCategoryTerm(ch: Char): (Category, Term) = {
    val floor = charFinder.floorEntry(ch)
    val ceiling = charFinder.ceilingEntry(ch)
    if (floor == null || ceiling == null) {
      (CharDef.defaultCategory, UnkDef.defaultTerm)
    } else if (floor.getValue == ceiling.getValue) {
      floor.getValue
    } else {
      (CharDef.defaultCategory, UnkDef.defaultTerm)
    }
  }
}
