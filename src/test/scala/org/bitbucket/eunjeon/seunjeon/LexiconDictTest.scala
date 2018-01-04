package org.bitbucket.eunjeon.seunjeon

import org.scalatest.FunSuite

class LexiconDictTest extends FunSuite {
  val TEST_RESOURCES_PATH = "src/test/resources"

  test("buildNNGTerm") {
    assert(
      "Morpheme(삼성전자,1784,3535,-100,WrappedArray(NNG, *, F, 삼성전자, *, *, *, *),COMMON,WrappedArray(N))" ==
      LexiconDict.buildNNGTerm("삼성전자", -100).toString
    )

    assert(
      "Morpheme(삼성전자,1784,3535,-100,WrappedArray(NNG, *, F, 삼성+전자, Compound, *, *, 삼성/NNG/*+전자/NNG/*),COMPOUND,WrappedArray(N))" ==
      LexiconDict.buildNNGTerm("삼성+전자", -100).toString
    )

    assert(
      "Morpheme(C++,1784,3535,-100,WrappedArray(NNG, *, *, C\\+\\+, *, *, *, *),COMMON,WrappedArray(N))" ==
      LexiconDict.buildNNGTerm("""C\+\+""", -100).toString
    )
  }

  test("load") {
    val csvs = Seq(
      "!,1794,3555,3559,SF,*,*,*,*,*,*,*",
      "(,1800,3560,-1362,SSO,*,*,*,*,*,*,*",
      "),1799,3559,-1788,SSC,*,*,*,*,*,*,*",
      """",",1792,3553,883,SC,*,*,*,*,*,*,*""",
      ".,1794,3555,3559,SF,*,*,*,*,*,*,*",
      "/,1792,3553,883,SC,*,*,*,*,*,*,*",
      "  :,1792,3553,883,SC,*,*,*,*,*,*,*",
      "?,1794,3555,3559,SF,*,*,*,*,*,*,*",
      "  [,1800,3560,-1362,SSO,*,*,*,*,*,*,*",
      "],1799,3559,-1788,SSC,*,*,*,*,*,*,*",
      "·,1792,3553,883,SC,*,*,*,*,*,*,*",
      "“,1800,3560,-1362,SSO,*,*,*,*,*,*,*",
      "”,1799,3559,-1788,SSC,*,*,*,*,*,*,*",
      "…,1793,3554,-1029,SE,*,*,*,*,*,*,*",
      "「,1800,3560,-1362,SSO,*,*,*,*,*,*,*",
      "」,1799,3559,-1788,SSC,*,*,*,*,*,*,*"
    )

    val result = new LexiconDict().loadFromIterator(csvs.toIterator)
    result.termDict.toSeq.foreach(println)

  }

  test("save and open") {
    val lexicons = """감자,1,2,100,NNG,*,F,감자,*,*,*,*,*
        |고구마,1,2,100,NNG,*,F,고구마,*,*,*,*,*
        | 고 구 ,1,2,100,NNG,*,F,고구마,*,*,*,*,*
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
    assert(Seq("고", "고구", "고구마") ==
      openLexiconDict.commonPrefixSearch("고구마").map(_.surface))
  }

  ignore("org.bitbucket.org.eunjeon.seunjeon.LexiconDict save performance") {
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
  }

  ignore("org.bitbucket.org.eunjeon.seunjeon.LexiconDict load performance") {
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
