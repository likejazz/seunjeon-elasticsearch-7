package org.bitbucket.eunjeon.seunjeon

import org.scalatest.FunSuite


class AnalyzerTest extends FunSuite {
  test("main test") {

    // FIXME: 분석이 이상하게 나옴.
    Analyzer.parse("아버지가방에들어가신다.").foreach(println)
    Analyzer.parse("아버지 가방에 들어가신다.").foreach(println)
    Analyzer.parse("하늘을 나는 자동차.").foreach(println)
    Analyzer.parse("존속하며,그직무를행한다.").foreach(println)
    Analyzer.parse("존속하며, 그 직무를 행한다.").foreach(println)
  }

  test("number test") {
    Analyzer.parse("12345한글67890 !@# ABCD").foreach { t: TermNode =>
      println(t)
    }
  }

  test("userdic-surface from file") {
    assert(Seq(
      "BOS:BOS",
      "버:NNP",
      "카:NNG",
      "충:NNG",
      "했:XSV+EP",
      "어:EF","?:SF",
      "EOS:EOS") == Analyzer.parse("버카충했어?").map(getSurfacePos))
    Analyzer.setUserDictDir("src/test/resources/userdict/")
    assert(Seq(
      "BOS:BOS",
      "버카충:NNG",
      "했:XSV+EP",
      "어:EF",
      "?:SF",
      "EOS:EOS") == Analyzer.parse("버카충했어?").map(getSurfacePos))
  }

  test("userdic-surface from iterator") {
    assert(Seq(
      "BOS:BOS",
      "어:IC",
      "그:NP",
      "로:JKB",
      "좀:MAG",
      "끌:VV",
      "고:EC",
      "있:VX",
      "어:EC",
      "봐:VX+EF",
      ".:SF",
      "EOS:EOS") == Analyzer.parse("어그로좀끌고있어봐.").map(getSurfacePos))
    Analyzer.setUserDict(Seq("어그로,-500", "갠소").toIterator)
    assert(Seq(
      "BOS:BOS",
      "어그로:NNG",
      "좀:MAG",
      "끌:VV",
      "고:EC",
      "있:VX",
      "어:EC",
      "봐:VX+EF",
      ".:SF",
      "EOS:EOS") == Analyzer.parse("어그로좀끌고있어봐.").map(getSurfacePos))
  }

  test("multi-char-dict") {
    Analyzer.setUserDict(Seq("삼성SDS", "LG CNS").toIterator)
    assert(Seq(
      "BOS:BOS",
      "삼성SDS:NNG",
      "EOS:EOS") == Analyzer.parse("삼성SDS").map(getSurfacePos))
    assert(Seq(
      "BOS:BOS",
      "LG CNS:NNG",
      "EOS:EOS") == Analyzer.parse("LG CNS").map(getSurfacePos))
  }

  def getSurfacePos(termNode:TermNode): String = {
    println(termNode)
    s"${termNode.term.surface}:${termNode.term.feature.head}"
  }
}
