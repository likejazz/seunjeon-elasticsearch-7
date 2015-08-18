package org.bitbucket.eunjeon.seunjeon

/**
 * Created by parallels on 8/18/15.
 */
object DicBuilder {
  def main(args: Array[String]): Unit = {
    val resourcePath = "src/main/resources"
    println("compiling lexicon dictionary...")
    val lexiconDict = new LexiconDict
    lexiconDict.loadFromCsvFiles("mecab-ko-dic")
    lexiconDict.save(
      resourcePath  + LexiconDict.lexiconResourceFile,
      resourcePath + LexiconDict.lexiconTrieResourceFile)

    println("compiling connection-cost dictionary...")
    val connectionCostDict = new ConnectionCostDict
    connectionCostDict.loadFromFile("mecab-ko-dic/matrix.def")
    connectionCostDict.save(
      resourcePath + ConnectionCostDict.resourceConnDicFile)

    println("complete")

  }

}
