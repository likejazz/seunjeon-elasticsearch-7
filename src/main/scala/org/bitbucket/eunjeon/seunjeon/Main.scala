package org.bitbucket.eunjeon.seunjeon

object Main extends App {
  val parser = getAnalyzer
  parse(parser)

  def parse(parser: Tokenizer): Unit = {
    for (ln <- io.Source.stdin.getLines()) {
      println(parser.parseText(ln).map(t => t.surface + ":" + t.feature(0)).mkString(","))
    }
  }

  def getAnalyzer: Tokenizer = {
    val lexiconDict = new LexiconDict().load()
    val connectionCostDict = new ConnectionCostDict().load()

    new Tokenizer(lexiconDict, connectionCostDict)
  }

}
