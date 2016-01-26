package org.bitbucket.eunjeon.seunjeon

import org.scalatest._

class DoubleArrayTrieTest extends FunSuite {

  test("build") {
    val daTrie = DoubleArrayTrieBuilder().
      add("a", 10).
      add("ab", 20).
      add("acd", 30).
      build()
    assert("10,20" == daTrie.commonPrefixSearch("ab").mkString(","))
  }

  test("write read") {
    DoubleArrayTrieBuilder().
      add("a", 10).
      add("ab", 20).
      add("abc", 30).
      add("가", 40).
      add("가나다", 70).
      build().write(new java.io.File("test_trie.dat"))

    val newDaTrie = DoubleArrayTrie(new java.io.File("test_trie.dat"))
    assert("10,20" == newDaTrie.commonPrefixSearch("ab").mkString(","))
    assert("40,70" == newDaTrie.commonPrefixSearch("가나다").mkString(","))
    assert("" == newDaTrie.commonPrefixSearch(" 가").mkString(","))
    assert("40" == newDaTrie.commonPrefixSearch("가 나다").mkString(","))
  }

}
