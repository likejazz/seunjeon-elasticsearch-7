package org.bitbucket.eunjeon.seunjeon

import java.util

import scala.collection.mutable


object CharSet extends Enumeration {
  /*
   TODO: char.def 사용하자.
   문자셋이 다른 것을 사전에 한 단어로 등록할 수 없다.
   */
  type CharSet = Value
  val SPACE, SYMBOL, NUMERIC, ALPHA, HANGUL, UNKNOWN, NOT = Value

    val charFinder = new util.TreeMap[Char, CharSet]()
    // SPACE
    charFinder.put('\u0020', CharSet.SPACE)
    charFinder.put('\u000D', CharSet.SPACE)
    charFinder.put('\u0009', CharSet.SPACE)
    charFinder.put('\u000B', CharSet.SPACE)
    charFinder.put('\u000A', CharSet.SPACE)

    // ASCII
    charFinder.put('\u0021', CharSet.SYMBOL)
    charFinder.put('\u002F', CharSet.SYMBOL)

    charFinder.put('\u0030', CharSet.NUMERIC)
    charFinder.put('\u0039', CharSet.NUMERIC)

    charFinder.put('\u003A', CharSet.SYMBOL)
    charFinder.put('\u0040', CharSet.SYMBOL)

    charFinder.put('\u0041', CharSet.ALPHA)
    charFinder.put('\u005A', CharSet.ALPHA)

    charFinder.put('\u005B', CharSet.SYMBOL)
    charFinder.put('\u0060', CharSet.SYMBOL)

    charFinder.put('\u0061', CharSet.ALPHA)
    charFinder.put('\u007A', CharSet.ALPHA)

    charFinder.put('\u007B', CharSet.SYMBOL)
    charFinder.put('\u007E', CharSet.SYMBOL)

    // Latin
    charFinder.put('\u00A1', CharSet.SYMBOL)
    charFinder.put('\u00BF', CharSet.SYMBOL)

    charFinder.put('\u00C0', CharSet.ALPHA)
    charFinder.put('\u00FF', CharSet.ALPHA)

    charFinder.put('\u0100', CharSet.ALPHA)
    charFinder.put('\u017F', CharSet.ALPHA)

    charFinder.put('\u0180', CharSet.ALPHA)
    charFinder.put('\u0236', CharSet.ALPHA)

    charFinder.put('\u1E00', CharSet.ALPHA)
    charFinder.put('\u1EF9', CharSet.ALPHA)

    // HANGUL
    charFinder.put('\uAC00', CharSet.HANGUL)
    charFinder.put('\uD7A3', CharSet.HANGUL)

    charFinder.put('\u1100', CharSet.HANGUL)  // Hangul Jamo
    charFinder.put('\u11FF', CharSet.HANGUL)

    charFinder.put('\u3130', CharSet.HANGUL)
    charFinder.put('\u318F', CharSet.HANGUL)  // Hangul Compatibility Jamo

  def splitCharSet(text: String): Seq[String] = {
    // TODO: 거지가 되었음. 꼭 리팩토링 하자.

    val result = new mutable.ListBuffer[String]
    var start = 0
    var curCharSet = CharSet.NOT
    val trimedText = text.trim
    trimedText.view.zipWithIndex.foreach { case (ch, idx) =>
      val charSet = getCharSet(ch)
      if (charSet != curCharSet) {
        if (curCharSet == CharSet.NOT) {
          curCharSet = charSet
        } else {
          result.append(trimedText.substring(start, idx))
          start = idx
          curCharSet = charSet
          if (charSet == CharSet.SPACE) {
            // TODO: space 일때 한칸만 가도 되는가? 있는만큼 가야하지 않는가?
            // 그리고 스페이스 자체도 노드에 추가하는건 어떤가?
            start += 1
          }
        }
      }
    }
    result.append(trimedText.substring(start, trimedText.length))
    result.filter(_.length > 0)
  }

  private def getCharSet(ch: Char): CharSet = {
    val floor = charFinder.floorEntry(ch)
    val ceiling = charFinder.ceilingEntry(ch)
    if (floor == null || ceiling == null) {
      CharSet.UNKNOWN
    } else if (floor.getValue == ceiling.getValue) {
      floor.getValue
    } else {
      CharSet.UNKNOWN
    }
  }

}
