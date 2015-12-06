package org.bitbucket.eunjeon.seunjeon

import org.scalatest.FunSuite

class CharDefTest extends FunSuite {
  test("splitChar") {
    assert("(  ,SPACE),(abc,ALPHA),(123,NUMERIC),( ,SPACE),(한글,HANGUL),(@#$,SYMBOL),( ,SPACE)" ==
      CharDefTest.convString(CharDef.splitCharSet("  abc123 한글@#$ ")))
  }

  test("emptyString") {
    assert("" == CharDefTest.convString(CharDef.splitCharSet("")))
    assert("(  ,SPACE)" == CharDefTest.convString(CharDef.splitCharSet("  ")))
  }

}

object CharDefTest {
  def convString(sets: Seq[CharSet]): String = {
    sets.map(charset => (charset.str, charset.morpheme.surface)).mkString(",")
  }
}
