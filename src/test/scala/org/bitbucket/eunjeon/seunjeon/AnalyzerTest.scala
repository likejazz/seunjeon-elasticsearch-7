package org.bitbucket.eunjeon.seunjeon

import org.scalatest.FunSuite


class AnalyzerTest extends FunSuite {
  test("main test") {
    Analyzer.parse("버카충했어? 형태소분석기입니다.").foreach { t: Term =>
      println(t)
    }
  }
}
