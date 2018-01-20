package org.bitbucket.eunjeon.seunjeon

import org.scalatest.FunSuite

class ReadmeTest extends FunSuite {
  test("readme") {
    // 형태소 분석
    Analyzer.parse("아버지가방에들어가신다.").foreach(println)

    // 어절 분석
    Analyzer.parseEojeol("아버지가방에들어가신다.").foreach(println)
    // or
    Analyzer.parseEojeol(Analyzer.parseParagraph("아버지가방에들어가신다.")).foreach(println)

    /**
      * 사용자 사전 추가
      * surface,cost
      *   surface: 단어명. '+' 로 복합명사를 구성할 수 있다.
      *           '+'문자 자체를 사전에 등록하기 위해서는 '\+'로 입력. 예를 들어 'C\+\+'
      *   cost: 단어 출연 비용. 작을수록 출연할 확률이 높다.
      */
    Analyzer.setUserDict(Seq("덕후", "버카충,-100", "낄끼+빠빠,-100", """C\+\+""").toIterator)
    Analyzer.parse("덕후냄새가 난다.").foreach(println)

    // 활용어 원형
    Analyzer.parse("빨라짐").map(_.deInflect()).foreach(println)

    // 복합명사 분해
    val ggilggi = Analyzer.parse("낄끼빠빠")
    ggilggi.foreach(println)  // 낄끼빠빠
    ggilggi.map(_.deCompound()).foreach(println)  // 낄끼+빠빠

    Analyzer.parse("C++").map(_.deInflect()).foreach(println) // C++
  }
}
