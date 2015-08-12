package org.bitbucket.eunjeon.seunjeon

import org.scalatest.FunSuite


class BuildDict extends FunSuite {

  test("compile lexicon dictionary") {
    val lexiconDict = new LexiconDict
    lexiconDict.loadFromCsvFiles("mecab-ko-dic")
    lexiconDict.save("src/main/resources/lexicon.dat", "src/main/resources/lexicon_trie.dat")
  }

  test("compile connection-cost dictionary") {
    val connectionCostDict = new ConnectionCostDict
    connectionCostDict.loadFromFile("mecab-ko-dic/matrix.def")
    connectionCostDict.save("src/main/resources/connection_cost.dat")
  }

}
