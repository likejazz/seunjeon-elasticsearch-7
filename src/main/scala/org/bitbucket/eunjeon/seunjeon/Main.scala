package org.bitbucket.eunjeon.seunjeon

object Main extends App {
  val parser = getAnalyzer
  parse(parser)

  def parse(parser: Parser): Unit = {
    for (ln <- io.Source.stdin.getLines()) {
      println(parser.parseText(ln).map(t => t.surface + ":" + t.feature(0)).mkString(","))
    }
  }

  def getAnalyzer: Parser = {
    val analyzer = new Parser()
    val lexiconDict = new LexiconDict
    lexiconDict.open()
    analyzer.setLexiconDict(lexiconDict)
    val connectionCostDict = new ConnectionCostDict
    connectionCostDict.open()
    analyzer.setConnectionCostDict(connectionCostDict)
    analyzer
  }

}
