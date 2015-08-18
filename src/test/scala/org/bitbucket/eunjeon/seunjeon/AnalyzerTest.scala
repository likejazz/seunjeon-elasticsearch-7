package org.bitbucket.eunjeon.seunjeon

import org.scalatest.FunSuite


class AnalyzerTest extends FunSuite {
  test("main test") {
    Analyzer.parse("버카충했어? 형태소분석기입니다. 철수와 영희는 사랑합니다.").foreach { t: Term =>
      println(t)
    }
  }

  test("number test") {
    Analyzer.parse("1234567890").foreach { t: Term =>
      println(t)
    }
  }

  test("erorr test") {
    val testString = "흐라"
    //val testString = "헬렌켈러"
    Analyzer.parse(testString).foreach { t: Term =>
      println(t)
    }
  }
}
