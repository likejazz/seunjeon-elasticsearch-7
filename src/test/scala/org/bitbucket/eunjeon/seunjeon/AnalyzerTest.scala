package org.bitbucket.eunjeon.seunjeon

import org.scalatest.FunSuite


class AnalyzerTest extends FunSuite {
  test("main test") {
    Analyzer.parse("붹붹붹이다.").foreach(println)

    Analyzer.parse("형태소분석기입니다. 사랑합니다.").foreach { term: Term => println(term) }
  }

  test("number test") {
    Analyzer.parse("12345한글67890 !@# ABCD").foreach { t: Term =>
      println(t)
    }
  }
}
