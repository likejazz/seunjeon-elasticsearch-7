package org.bitbucket.eunjeon.seunjeon

import org.scalatest.FunSuite


class NngUtilTest extends FunSuite {
  test("nng left-id") {
    println(NngUtil.nngLeftId)

  }

  test("nng right-id") {
    println(NngUtil.nngFRightId)
    println(NngUtil.nngTRightId)
  }

}
