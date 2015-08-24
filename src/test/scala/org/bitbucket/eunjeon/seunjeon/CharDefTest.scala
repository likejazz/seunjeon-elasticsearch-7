package org.bitbucket.eunjeon.seunjeon

import org.scalatest.FunSuite

class CharDefTest extends FunSuite {
  test("splitChar") {
    assert("(abc,ALPHA),(123,NUMERIC),(한글,HANGUL),(@#$,SYMBOL)" ==
      CharDefTest.convString(CharDef.splitCharSet("  abc123 한글@#$ ")))
  }

  test("emptyString") {
    assert("" == CharDefTest.convString(CharDef.splitCharSet("")))
    assert("" == CharDefTest.convString(CharDef.splitCharSet("  ")))
  }

}

object CharDefTest {
  def convString(sets: Seq[CharSet]): String = {
    sets.map(charset => (charset.str, charset.term.surface)).mkString(",")
  }
}
