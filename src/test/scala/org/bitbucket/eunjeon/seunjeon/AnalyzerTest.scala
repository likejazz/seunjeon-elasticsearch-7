package org.bitbucket.eunjeon.seunjeon

import org.scalatest.{BeforeAndAfter, FunSuite}


class AnalyzerTest extends FunSuite with BeforeAndAfter {
  before {
    Analyzer.resetUserDict()
  }

  test("main test") {
    // FIXME: 분석이 이상하게 나옴.
    Analyzer.parse("하늘을 나는 자동차.").foreach(println)
    // TODO: double-array-trie library bug.
//    Analyzer.parse("모두의마블\uffff전설의 5시간 및 보석 교체").foreach(println)
  }

  test("penalty cost") {
    assert("아버지,가,방,에,들어가,신다,." ==
      Analyzer.parse("아버지가방에들어가신다.").map(_.morpheme.surface).mkString(","))
    assert("아버지,가방,에,들어가,신다,." ==
      Analyzer.parse("아버지 가방에 들어가신다.").map(_.morpheme.surface).mkString(","))
  }

  test("number test") {
    Analyzer.parse("12345한글67890 !@# ABCD").foreach { t: LNode =>
      println(t)
    }
  }

  test("userdic-surface from file") {
    assert(Seq(
      "버:NNP",
      "카:NNG",
      "충:NNG",
      "했:XSV+EP",
      "어:EF","?:SF") == Analyzer.parse("버카충했어?").map(getSurfacePos))
    Analyzer.setUserDictDir("src/test/resources/userdict/")
    assert(Seq(
      "버카충:NNG",
      "했:XSV+EP",
      "어:EF",
      "?:SF") == Analyzer.parse("버카충했어?").map(getSurfacePos))
  }

  test("userdic-surface from iterator") {
    assert(Seq(
      "어:IC",
      "그:NP",
      "로:JKB",
      "좀:MAG",
      "끌:VV",
      "고:EC",
      "있:VX",
      "어:EC",
      "봐:VX+EF",
      ".:SF") == Analyzer.parse("어그로좀끌고있어봐.").map(getSurfacePos))
    Analyzer.setUserDict(Seq("어그로,-500", "갠소").toIterator)
    assert(Seq(
      "어그로:NNG",
      "좀:MAG",
      "끌:VV",
      "고:EC",
      "있:VX",
      "어:EC",
      "봐:VX+EF",
      ".:SF") == Analyzer.parse("어그로좀끌고있어봐.").map(getSurfacePos))
  }

  test("multi-char-dict") {
    Analyzer.setUserDict(Seq("삼성SDS", "LG CNS").toIterator)
    assert(Seq(
      "삼성SDS:NNG") == Analyzer.parse("삼성SDS").map(getSurfacePos))
    assert(Seq(
      "LG CNS:NNG") == Analyzer.parse("LG CNS").map(getSurfacePos))
  }

  test("README example1") {
    Analyzer.parse("아버지가방에들어가신다.").foreach(println)
  }

  test("READ example2") {
    println("# BEFORE")
    Analyzer.parse("덕후냄새가 난다.").foreach(println)
    Analyzer.setUserDictDir("src/test/resources/userdict/")
    println("# AFTER ")
    Analyzer.parse("덕후냄새가 난다.").foreach(println)
  }

  test("README example3") {
    println("# BEFORE")
    Analyzer.parse("덕후냄새가 난다.").foreach(println)
    Analyzer.setUserDict(Seq("덕후", "버카충,-100", "낄끼빠빠").toIterator)
    println("# AFTER ")
    Analyzer.parse("덕후냄새가 난다.").foreach(println)
  }

  test("README eojeol") {
    Analyzer.parseEojeol("아버지가방에들어가신다.").map(_.surface).foreach(println)
    Analyzer.parseEojeol(Analyzer.parse("아버지가방에들어가신다.")).foreach(println)
  }

  test("empty eojeol") {
    assert(Seq[Eojeol]() == Analyzer.parseEojeol(""))
  }

  test("dePreAnalysis") {
    val result1 = Analyzer.parse("은전한닢프로젝트")
    assert("은전+한+닢+프로젝트" == result1.map(_.morpheme.surface).mkString("+"))
    result1.foreach(println)

    val result2 = Analyzer.parse("은전한닢프로젝트", preAnalysis=false)
    assert("은전한닢+프로젝트" == result2.map(_.morpheme.surface).mkString("+"))
    result2.foreach(println)
  }

  test("functation") {
    Analyzer.parse("《재규어》.").foreach(println)
    Analyzer.parse(" ^^ 55 《삐리리~ 불어봐! 재규어》.").foreach(println)
  }

  test("multi line") {
    Analyzer.parse("가\n나").foreach(println)
  }

  def getSurfacePos(termNode:LNode): String = {
    println(termNode)
    s"${termNode.morpheme.surface}:${termNode.morpheme.feature.head}"
  }
}
