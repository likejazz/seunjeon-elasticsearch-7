package org.bitbucket.eunjeon.seunjeon

import org.scalatest.FunSuite


class LexiconDictTest extends FunSuite {

  test("save and open") {
    val lexicons = """감자,1,2,100,NNG,*,F,감자,*,*,*,*,*
        |고구마,1,2,100,NNG,*,F,고구마,*,*,*,*,*
        |고,1,2,100,NNG,*,F,고,*,*,*,*,*
        |구마,1,2,100,NNG,*,F,구마,*,*,*,*,*
        |오징어,1,2,100,NNG,*,F,오징어,*,*,*,*,*""".stripMargin
    val saveLexiconDict = new LexiconDict
    saveLexiconDict.loadFromString(lexicons)
    saveLexiconDict.save("." + LexiconDict.termDictResourceFile,
                         "." + LexiconDict.dictMapperResourceFile,
                         "." + LexiconDict.trieResourceFile)

    val openLexiconDict = new LexiconDict
    openLexiconDict.load("." + LexiconDict.termDictResourceFile,
                         "." + LexiconDict.dictMapperResourceFile,
                         "." + LexiconDict.trieResourceFile)
    val result = openLexiconDict.commonPrefixSearch("고구마")
    assert("Term(고,1,2,100,NNG,*,F,고,*,*,*,*,*),Term(고구마,1,2,100,NNG,*,F,고구마,*,*,*,*,*)" == result.mkString(","))
  }

  ignore("org.bitbucket.org.eunjeon.seunjeon.LexiconDict load performance") {
    {
      val startTime = System.nanoTime()
      val lexiconDict = new LexiconDict
      lexiconDict.loadFromDir("mecab-ko-dic")
      val endTime = System.nanoTime()
      val elapsedTime = (endTime - startTime)
      println(s"$elapsedTime ns")
      lexiconDict.save("src/test/resources/lexicon.dat", "src/test/resources/lexicon_trie.dat")
    }
    {
      val startTime = System.nanoTime()
      val lexiconDict = new LexiconDict
      lexiconDict.load()
      val endTime = System.nanoTime()
      val elapsedTime = (endTime - startTime)
      println(s"$elapsedTime ns")
    }
  }
}
