package org.bitbucket.eunjeon.seunjeon

import org.scalatest._

/**
  * Created by parallels on 11/14/15.
  */
class PosIdTest extends FunSuite with BeforeAndAfter {
  test("PosId get") {
    var term = "가가방,1784,3537,2943,NNG,*,T,가가방,Compound,*,*,가가/NNG/*+방/NNG/*".split(",")
    assert(1 == PosId(term.slice(4, term.length)))
    term = "힘,1784,3537,1889,NNG,*,T,힘,*,*,*,*".split(",")
    assert(150 == PosId("NNG,*,T,곽밥,*,*,*,*".split(",")))
  }

}
