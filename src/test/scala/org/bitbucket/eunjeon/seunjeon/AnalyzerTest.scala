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
}
