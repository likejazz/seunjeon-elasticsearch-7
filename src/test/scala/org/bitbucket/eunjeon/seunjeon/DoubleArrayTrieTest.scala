package org.bitbucket.eunjeon.seunjeon

import org.scalatest._

class DoubleArrayTrieTest extends FunSuite {

  test("build") {
    val trie = SimpleTrie()
    trie.add("a", 10)
    trie.add("ab", 20)
    trie.add("acd", 30)

    val daTrie = DoubleArrayTrie(trie)

  }

}
