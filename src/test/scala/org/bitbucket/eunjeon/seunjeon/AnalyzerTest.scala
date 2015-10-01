package org.bitbucket.eunjeon.seunjeon

import org.scalatest.FunSuite


class AnalyzerTest extends FunSuite {
  test("main test") {
    //Analyzer.parse("붹붹붹이다.").foreach(println)

    // FIXME: 분석이 이상하게 나옴.
    Analyzer.parse("아버지가방에들어가신다.").foreach(println)
    Analyzer.parse("아버지 가방에 들어가신다.").foreach(println)
    Analyzer.parse("하늘을 나는 자동차.").foreach(println)
  }

  test("number test") {
    Analyzer.parse("12345한글67890 !@# ABCD").foreach { t: TermNode =>
      println(t)
    }
  }

  test("userdic-surface from file") {
    // TODO: 테스트용 connection-cost dict 를 넣을수있게해서 unit test 을 가능하게 하자
    // TODO: assert...
    println("# BEFORE")
    Analyzer.parse("버카충했어?").foreach(println)
    Analyzer.setUserDictDir("src/test/resources/userdict/")
    println("# AFTER ")
    Analyzer.parse("버카충했어?").foreach(println)
  }

  test("userdic-surface from iterator") {
    // TODO: assert...
    println("# BEFORE")
    Analyzer.parse("어그로좀끌고있어봐.").foreach(println)
    Analyzer.setUserDict(Seq("어그로,-500", "갠소").toIterator)
    println("# AFTER ")
    Analyzer.parse("어그로좀끌고있어봐.").foreach(println)
  }
}
