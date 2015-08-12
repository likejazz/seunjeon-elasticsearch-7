package org.bitbucket.eunjeon.seunjeon

import org.scalatest.FunSuite


class BuildDict extends FunSuite {
  val resourcePath = "src/main/resources"

  test("compile lexicon dictionary") {
    val lexiconDict = new LexiconDict
    lexiconDict.loadFromCsvFiles("mecab-ko-dic")
    lexiconDict.save(
      resourcePath  + LexiconDict.lexiconResourceFile,
      resourcePath + LexiconDict.lexiconTrieResourceFile)
  }

  test("compile connection-cost dictionary") {
    val connectionCostDict = new ConnectionCostDict
    connectionCostDict.loadFromFile("mecab-ko-dic/matrix.def")
    connectionCostDict.save(
      resourcePath + ConnectionCostDict.resourceConnDicFile)
  }

}
