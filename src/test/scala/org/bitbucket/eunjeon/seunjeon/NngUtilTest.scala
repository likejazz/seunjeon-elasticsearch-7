package org.bitbucket.eunjeon.seunjeon

import org.scalatest.FunSuite


class NngUtilTest extends FunSuite {
  test("nng left-id") {
    assert(1784 == NngUtil.nngLeftId)
  }

  test("nng right-id") {
    assert(3535 == NngUtil.nngRightId)
    assert(3536 == NngUtil.nngFRightId)
    assert(3537 == NngUtil.nngTRightId)
  }
}
