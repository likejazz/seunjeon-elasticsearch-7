package org.bitbucket.eunjeon.seunjeon

import scala.collection.JavaConverters._

object Analyzer {
  val tokenizer = {
    val lexiconDict = new LexiconDict().load()
    val connectionCostDict = new ConnectionCostDict().load()

    new Tokenizer(lexiconDict, connectionCostDict)
  }

  def parse(sentence: String): Seq[Term] = {
    tokenizer.parseText(sentence)
  }

  def parseJava(sentence: String): java.util.List[Term] = {
    parse(sentence).asJava

  }
}
