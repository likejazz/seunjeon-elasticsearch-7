package org.bitbucket.eunjeon.seunjeon

import org.scalatest._

class SimpleTrieTest extends FunSuite {

  test("add") {
    val trie = SimpleTrie()
    trie.add("a", 10)
    trie.add("ab", 20)
    trie.add("acd", 30)

    assert("""[a]""" == trie.root.children.keySet().toString)
    assert(10 == trie.root.children.get('a').value)
    assert("""[b, c]""" == trie.root.children.get('a').children.keySet().toString)
    assert(-1 == trie.root.children.get('a').children.get('c').value)
    assert(30 == trie.root.children.get('a').children.get('c').children.get('d').value)
  }

}