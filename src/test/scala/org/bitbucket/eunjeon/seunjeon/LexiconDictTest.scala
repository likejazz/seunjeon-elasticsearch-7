package org.bitbucket.eunjeon.seunjeon

import org.scalatest.FunSuite

class LexiconDictTest extends FunSuite {
  val TEST_RESOURCES_PATH = "src/test/resources"

  test("save and open") {
    val lexicons = """감자,1,2,100,NNG,*,F,감자,*,*,*,*,*
        |고구마,1,2,100,NNG,*,F,고구마,*,*,*,*,*
        |고,1,2,100,NNG,*,F,고,*,*,*,*,*
        |구마,1,2,100,NNG,*,F,구마,*,*,*,*,*
        |오징어,1,2,100,NNG,*,F,오징어,*,*,*,*,*""".stripMargin
    val saveLexiconDict = new LexiconDict
    saveLexiconDict.loadFromString(lexicons)
    saveLexiconDict.save(TEST_RESOURCES_PATH + "/" + DictBuilder.TERM_DICT_FILENAME,
                         TEST_RESOURCES_PATH + "/" + DictBuilder.DICT_MAPPER_FILENAME,
                         TEST_RESOURCES_PATH + "/" + DictBuilder.TERM_TRIE_FILENAME)

    val openLexiconDict = new LexiconDict
    openLexiconDict.load(TEST_RESOURCES_PATH + "/" + DictBuilder.TERM_DICT_FILENAME,
                         TEST_RESOURCES_PATH + "/" + DictBuilder.DICT_MAPPER_FILENAME,
                         TEST_RESOURCES_PATH + "/" + DictBuilder.TERM_TRIE_FILENAME)
    assert(Seq("고", "고구마") ==
      openLexiconDict.commonPrefixSearch("고구마").map(_.surface))
  }

  ignore("org.bitbucket.org.eunjeon.seunjeon.LexiconDict load performance") {
    {
      val startTime = System.nanoTime()
      val lexiconDict = new LexiconDict
      lexiconDict.loadFromDir("mecab-ko-dic")
      val endTime = System.nanoTime()
      val elapsedTime = (endTime - startTime)
      println(s"$elapsedTime ns")
      lexiconDict.save(TEST_RESOURCES_PATH + "/" + DictBuilder.TERM_DICT_FILENAME,
        TEST_RESOURCES_PATH + "/" + DictBuilder.DICT_MAPPER_FILENAME,
        TEST_RESOURCES_PATH + "/" + DictBuilder.TERM_TRIE_FILENAME)
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
