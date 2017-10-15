package org.bitbucket.eunjeon.seunjeon

import java.io.{FileInputStream, InputStream}

import org.scalatest._

import scala.io.Source

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
      add("bc", 35).
      add("가", 40).
      add("가나다", 70).
      build().write(new java.io.File("test_trie.dat"))

    val newDaTrie = DoubleArrayTrie(new java.io.File("test_trie.dat"))
    assert("10,20,30" == newDaTrie.commonPrefixSearch("abc").mkString(","))
    assert("10,20" == newDaTrie.commonPrefixSearch("ab").mkString(","))
    assert("40,70" == newDaTrie.commonPrefixSearch("가나다").mkString(","))
    assert("" == newDaTrie.commonPrefixSearch(" 가").mkString(","))
    assert("40" == newDaTrie.commonPrefixSearch("가 나다").mkString(","))
  }

  test("trie performance") {
    val cl = classOf[DoubleArrayTrieTest].getClassLoader
    val inputStream = cl.getResourceAsStream("long_sentence.txt")

    val lines = Source.fromInputStream(inputStream, "UTF-8").getLines().toSeq.filter(_.nonEmpty)
    val builder = DoubleArrayTrieBuilder()
    lines.foreach(builder.add(_, 0))
    val trie = builder.build()
    println(trie.trieInfo)

    (0 to 10000).foreach { c =>
      lines.foreach( l =>
        assert(trie.commonPrefixSearch(l).nonEmpty, s"'$l' not found")
      )
    }
  }

}
