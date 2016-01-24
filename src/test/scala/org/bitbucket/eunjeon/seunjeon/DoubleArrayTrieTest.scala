package org.bitbucket.eunjeon.seunjeon

import org.scalatest._

class DoubleArrayTrieTest extends FunSuite {

  test("build") {
    val trie = SimpleTrie()
    trie.add("a", 10)
    trie.add("ab", 20)
    trie.add("acd", 30)

    val daTrie = DoubleArrayTrie(trie)
    assert("10,20" == daTrie.commonPrefixSearch("ab").mkString(","))
  }

  test("write read") {
    val trie = SimpleTrie()
    trie.add("a", 10)
    trie.add("ab", 20)
    trie.add("abc", 30)
    trie.add("가", 40)
    trie.add("가나다", 70)

    val daTrie = DoubleArrayTrie(trie)
    daTrie.write(new java.io.File("test_trie.dat"))

    val newDaTrie = DoubleArrayTrie(new java.io.File("test_trie.dat"))
//    assert("10,20" == newDaTrie.commonPrefixSearch("ab").mkString(","))
    assert("40,70" == newDaTrie.commonPrefixSearch("가나다").mkString(","))
//    assert("" == newDaTrie.commonPrefixSearch(" 가").mkString(","))
//    assert("40" == newDaTrie.commonPrefixSearch("가 나다").mkString(","))
  }

}
