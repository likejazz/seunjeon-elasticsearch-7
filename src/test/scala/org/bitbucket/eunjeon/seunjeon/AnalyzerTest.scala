package org.bitbucket.eunjeon.seunjeon

import org.scalatest.FunSuite


class AnalyzerTest extends FunSuite {
  test("main test") {
    Analyzer.parse("형태소분석기입니다. 사랑합니다.").foreach { term: Term =>
      println(term)
    }
  }

  test("number test") {
    Analyzer.parse("1234567890 !@# ABCD").foreach { t: Term =>
      println(t)
    }
  }
}
