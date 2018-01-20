package org.bitbucket.eunjeon.seunjeon

import org.scalatest.FunSuite

class CharSetDefTest extends FunSuite {
  test("splitChar") {
    assert(
      CharSetDefTest.convString(CharSetDef.splitCharSet("  abc123 한글@#$ ")) ===
        "(  ,SPACE),(abc,ALPHA),(123,NUMERIC),( ,SPACE),(한글,HANGUL),(@#$,SYMBOL),( ,SPACE)")
  }

  test("emptyString") {
    assert("" == CharSetDefTest.convString(CharSetDef.splitCharSet("")))
    assert("(  ,SPACE)" == CharSetDefTest.convString(CharSetDef.splitCharSet("  ")))
  }

  test("parseLine") {
    val str = "0x2E80..0x2EF3  HANJA # CJK Raidcals Supplement"
    assert(CharSetDef.parseCharset(str) ===
      (0x2E80,0x2EF3,"HANJA"))

    val str2 = "0x3005          HANJA # IDEOGRAPHIC ITERATION MARK"
    assert(CharSetDef.parseCharset(str2) ===
      (0x3005,0x3005,"HANJA"))
  }

}

object CharSetDefTest {
  def convString(sets: Seq[CharSet]): String = {
    sets.map(charset => (charset.str, charset.morpheme.getSurface)).mkString(",")
  }
}
