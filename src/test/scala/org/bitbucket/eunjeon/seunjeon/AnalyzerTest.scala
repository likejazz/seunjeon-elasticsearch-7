package org.bitbucket.eunjeon.seunjeon

import org.scalatest.{BeforeAndAfter, FunSuite}

import scala.io.Source


class AnalyzerTest extends FunSuite with BeforeAndAfter {
  before {
    Analyzer.resetUserDict()
  }

  test("main test") {
    // FIXME: 분석이 이상하게 나옴.
    Analyzer.parse("* 프랑스어: Gabon – République Gabonaise").foreach(println)
    // TODO: double-array-trie library bug.
//        Analyzer.parse("모두의마블\uffff전설의 5시간 및 보석 교체").foreach(println)
  }

  test("penalty cost") {
    Analyzer.parse("아버지가방에들어가신다.").foreach(println)
    assert(
      Analyzer.parse("아버지가 방에 들어가신다.").map(_.morpheme.getSurface).mkString(",") ===
        "아버지,가,방,에,들어가,신다,.")

    assert(
      Analyzer.parse("아버지 가방에 들어가신다.").map(_.morpheme.getSurface).mkString(",") ===
        "아버지,가방,에,들어가,신다,.")
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
      "어:EF", "?:SF") == Analyzer.parse("버카충했어?").map(getSurfacePos))
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

  test("empty eojeol") {
    assert(Seq[Eojeol]() == Analyzer.parseEojeol(""))
  }

  test("dePreAnalysis") {
    val result1 = Analyzer.parse("은전한닢프로젝트").toSeq
    assert("은전+한+닢+프로젝트" == result1.map(_.morpheme.getSurface).mkString("+"))
    result1.foreach(println)

    val result2 = Analyzer.parse("은전한닢프로젝트", preAnalysis = false).toSeq
    assert("은전한닢+프로젝트" == result2.map(_.morpheme.getSurface).mkString("+"))
    result2.foreach(println)
  }

  test("functation") {
    Analyzer.parse("""F = \frac{10^7}{(4\\pi)^2}""").foreach(println)
    Analyzer.parse("《재규어》.").foreach(println)
    Analyzer.parse(" ^^ 55 《삐리리~ 불어봐! 재규어》.").foreach(println)
  }

  test("multi line") {
    Analyzer.parse("가\n나").foreach(println)
  }

  test("long text") {
    val longText = Source.fromInputStream(getClass.getResourceAsStream("/path_disconnect.txt"), "UTF-8").mkString
    val morphemes = Analyzer.parse(longText)
  }

  test("unk bug") {
    var result:Seq[LNode] = null

    result = Analyzer.parse("농어촌체험휴양하누리마을").toSeq
    assert(result.head.morpheme.getSurface == "농어촌")

    Analyzer.setMaxUnkLength(100)
    result = Analyzer.parse("농어촌체험휴양하누리마을").toSeq
    assert(result.head.morpheme.getSurface == "농어촌체험휴양하누리마을")

    Analyzer.setMaxUnkLength(8)
    result = Analyzer.parse("농어촌체험휴양하누리마을").toSeq
    assert(result.head.morpheme.getSurface == "농어촌")
  }

  test("hanja") {
    assert(
      Analyzer.parse("柳英浩 郭鎬英 abc").map(getSurfacePos) ===
        List("柳英浩:SH", "郭鎬英:SH", "abc:SL")
    )
  }

  def getSurfacePos(termNode: LNode): String = {
    s"${termNode.morpheme.getSurface}:${termNode.morpheme.getFeature.head}"
  }
}
