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
case class CharSet(str: String, rlength: Int, category: Category, morpheme: Morpheme)

// TODO
object UnkDef {
  // defaultMorpheme, morpehmes 순서가 중요하다.. refactoring하자.
  var defaultMorpheme: Morpheme = null
  val terms = buildUnk

  def buildUnk: mutable.Map[String, Morpheme] = {
    val morphemes = mutable.Map[String, Morpheme]()
    val inputStream = classOf[CharSet].getResourceAsStream(DictBuilder.UNK_DEF)
    Source.fromInputStream(inputStream).getLines().foreach { line =>
      val l = line.split(",")
      if (l(0) == "DEFAULT") {
        val feature = l.slice(4, l.size)
        defaultMorpheme = Morpheme(l(0), l(1).toShort, l(2).toShort, l(3).toShort, feature, MorphemeType(feature), Pos.poses(feature))
      } else {
        val feature = l.slice(4, l.size)
        morphemes(l(0)) = Morpheme(l(0), l(1).toShort, l(2).toShort, l(3).toShort, feature, MorphemeType(feature), Pos.poses(feature))
      }
    }
    morphemes
  }

  def apply(name: String): Option[Morpheme] = {
    terms.get(name)
  }
}

case class Category(invoke:Boolean, group:Boolean, length:Int)

// TODO: charset, category 구조가 잘 안잡힌듯.. 교통정리가 필요함.
object CharDef {
  var defaultCategory:Category = null
  lazy val charFinder:util.TreeMap[Char, (Category, Morpheme)] =  {
    val categories = mutable.Map[String, Category]()
    val charMap = new util.TreeMap[Char, (Category, Morpheme)]()
    val inputStream = classOf[CharSet].getResourceAsStream("/char.def")
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
    // TODO: ArrayBuffer 크기를 미리 잡아두어서 속도는 빠르겠지만 메모리 많이 사용할 수도 있음.
    val charsets = new mutable.ArrayBuffer[CharSet](text.length)
    if (text.length == 0) {
      return charsets
    }
    var start = 0
    var curCategoryTerm: (Category, Morpheme) = null
    text.view.zipWithIndex.foreach { case (ch, idx) =>
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
    val floor = charFinder.floorEntry(ch)
    val ceiling = charFinder.ceilingEntry(ch)
    if (floor == null || ceiling == null) {
      (CharDef.defaultCategory, UnkDef.defaultMorpheme)
    } else if (floor.getValue == ceiling.getValue) {
      floor.getValue
    } else {
      (CharDef.defaultCategory, UnkDef.defaultMorpheme)
    }
  }
}
