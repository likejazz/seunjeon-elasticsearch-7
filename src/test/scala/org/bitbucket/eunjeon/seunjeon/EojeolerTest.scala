package org.bitbucket.eunjeon.seunjeon

import org.scalatest.FunSuite


class EojeolerTest extends FunSuite {

  test("Eojeol build") {
    //val analyzed = Analyzer.parse("아버지가방에들어가신다.")
    val analyzed = Analyzer.parseEojeol("유영호군과김고은양이결혼했습니다. 축하해주세요.")
    assert(
      List("유영호", "군과", "김고은", "양이", "결혼했습니다", ".", "축하해", "주세요", ".") ===
        analyzed.map(_.surface))
  }

}
