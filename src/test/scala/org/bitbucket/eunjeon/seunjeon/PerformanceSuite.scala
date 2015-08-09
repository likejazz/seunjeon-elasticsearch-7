package org.bitbucket.eunjeon.seunjeon

import org.scalatest.{BeforeAndAfter, FunSuite}


class PerformanceSuite extends FunSuite with BeforeAndAfter {
  var dictionary: Parser = null

  before {
    dictionary = new Parser()

    val lexiconDict = new LexiconDict
    lexiconDict.open()
    dictionary.setLexiconDict(lexiconDict)

    val connectionCostDict = new ConnectionCostDict
    connectionCostDict.open()
    dictionary.setConnectionCostDict(connectionCostDict)
  }

  test("sentence") {
    val result = dictionary.parseText("어제 앵꼬되었잖아. 버카충했어?").map(t => t.surface + ":" + t.feature(0)).mkString(",")
    println(result)
  }

  test("performance") {
    val times = 10000
    val startTime = System.nanoTime()
    for (i <- 0 until times) {
      dictionary.parseText("안녕하세요. 형태소분석기입니다. 서울에서 살고있습니다.")
    }
    val endTime = System.nanoTime()
    val elapsedTime = (endTime - startTime) / times
    println(elapsedTime)
    println(s"$elapsedTime ns")
  }


}
