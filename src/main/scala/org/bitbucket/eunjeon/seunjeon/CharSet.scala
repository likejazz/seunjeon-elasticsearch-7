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
import org.bitbucket.eunjeon.seunjeon.Category.Category

import scala.collection.mutable


// TODO: unk.def 파일에서 좌/우/비용 찾아서 넣어주자.
case class CharSet(str: String, category: Category)

// TODO
object Unk {

}

object Category extends Enumeration {
  /*
   TODO: char.def 사용하자.
   */
  type Category = Term
  val DEFAULT = Term(null, 1801, 3561, 3633, "SY")
  val SPACE = Term(null, 1798, 3558, 917, "SP")
  val ALPHA = Term(null, 1796, 3535, 850, "SL")
  val CYRILLIC = Term(null, 1796, 3535, 850, "SL")
  val GREEK = Term(null, 1796, 3535, 850, "SL")
  val HANGUL = Term(null,1803, 3564, 9396, "UNKNOWN")
  val HANJA = Term(null,1795, 3556, -887, "SH")
  val HIRAGANA = Term(null, 1796, 3535, 850, "SL")
  val KANJI = Term(null, 1796, 3535, 850, "SL")
  val HANJANUMERIC = Term(null, 1795, 3556, -887, "SH")
  val KATAKANA = Term(null, 1796, 3535, 850, "SL")
  val NUMERIC = Term(null, 1797, 3557, 3757, "SN")
  val SYMBOL = Term(null, 1801, 3561, 3633, "SY")
  val NOT = null

  val charFinder = new util.TreeMap[Char, Category]()
  // SPACE
  charFinder.put('\u0020', Category.SPACE)
  charFinder.put('\u000D', Category.SPACE)
  charFinder.put('\u0009', Category.SPACE)
  charFinder.put('\u000B', Category.SPACE)
  charFinder.put('\u000A', Category.SPACE)

  // ASCII
  charFinder.put('\u0021', Category.SYMBOL)
  charFinder.put('\u002F', Category.SYMBOL)

  charFinder.put('\u0030', Category.NUMERIC)
  charFinder.put('\u0039', Category.NUMERIC)

  charFinder.put('\u003A', Category.SYMBOL)
  charFinder.put('\u0040', Category.SYMBOL)

  charFinder.put('\u0041', Category.ALPHA)
  charFinder.put('\u005A', Category.ALPHA)

  charFinder.put('\u005B', Category.SYMBOL)
  charFinder.put('\u0060', Category.SYMBOL)

  charFinder.put('\u0061', Category.ALPHA)
  charFinder.put('\u007A', Category.ALPHA)

  charFinder.put('\u007B', Category.SYMBOL)
  charFinder.put('\u007E', Category.SYMBOL)

  // Latin
  charFinder.put('\u00A1', Category.SYMBOL)
  charFinder.put('\u00BF', Category.SYMBOL)

  charFinder.put('\u00C0', Category.ALPHA)
  charFinder.put('\u00FF', Category.ALPHA)

  charFinder.put('\u0100', Category.ALPHA)
  charFinder.put('\u017F', Category.ALPHA)

  charFinder.put('\u0180', Category.ALPHA)
  charFinder.put('\u0236', Category.ALPHA)

  charFinder.put('\u1E00', Category.ALPHA)
  charFinder.put('\u1EF9', Category.ALPHA)

  // HANGUL
  charFinder.put('\uAC00', Category.HANGUL)
  charFinder.put('\uD7A3', Category.HANGUL)

  charFinder.put('\u1100', Category.HANGUL)  // Hangul Jamo
  charFinder.put('\u11FF', Category.HANGUL)

  charFinder.put('\u3130', Category.HANGUL)
  charFinder.put('\u318F', Category.HANGUL)  // Hangul Compatibility Jamo

  def splitCharSet(text: String): Seq[CharSet] = {
    // TODO: 거지가 되었음. 꼭 리팩토링 하자.

    // TODO: unk.def, char.def 보고 category 만들어야 함.
    val result = new mutable.ListBuffer[CharSet]
    var start = 0
    var curCategory: Category = Category.NOT
    val trimedText = text.trim
    trimedText.view.zipWithIndex.foreach { case (ch, idx) =>
      val charSet: Category = getCharSet(ch)
      if (charSet != curCategory) {
        if (curCategory == Category.NOT) {
          curCategory = charSet
        } else {
          //result.append(trimedText.substring(start, idx))
          result.append(CharSet(trimedText.substring(start, idx), curCategory))
          start = idx
          curCategory = charSet
          if (charSet == Category.SPACE) {
            // TODO: space 일때 한칸만 가도 되는가? 있는만큼 가야하지 않는가?
            // 그리고 스페이스 자체도 노드에 추가하는건 어떤가?
            start += 1
          }
        }
      }
    }
    result.append(CharSet(trimedText.substring(start, trimedText.length), curCategory))
    result.filter(_.str.length > 0)
  }

  private def getCharSet(ch: Char): Category = {
    val floor = charFinder.floorEntry(ch)
    val ceiling = charFinder.ceilingEntry(ch)
    if (floor == null || ceiling == null) {
      Category.DEFAULT
    } else if (floor.getValue == ceiling.getValue) {
      floor.getValue
    } else {
      Category.DEFAULT
    }
  }

}
