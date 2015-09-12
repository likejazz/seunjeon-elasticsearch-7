package org.bitbucket.eunjeon.seunjeon

import org.scalatest.FunSuite


class AnalyzerTest extends FunSuite {
  test("main test") {
    //Analyzer.parse("붹붹붹이다.").foreach(println)

    // FIXME: 분석이 이상하게 나옴.
    Analyzer.parse("아버지가방에들어가신다.").foreach(println)
  }

  test("number test") {
    Analyzer.parse("12345한글67890 !@# ABCD").foreach { t: Term =>
      println(t)
    }
  }

  test("userdic-surface from file") {
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
    Analyzer.parse("버카충했어?").foreach(println)
    Analyzer.setUserDict(Seq("버카충", "낄끼빠빠").toIterator)
    println("# AFTER ")
    Analyzer.parse("버카충했어?").foreach(println)
  }
}
