package org.bitbucket.eunjeon.seunjeon

import org.scalatest.FunSuite


class EojeolerTest extends FunSuite {

  test("Eojeol build") {
    val eojeols = Eojeoler.build(Analyzer.parse("아버지 가방에 들어가신다."))
    println(eojeols)
  }

}
