package org.bitbucket.eunjeon.seunjeon

import org.scalatest.{BeforeAndAfter, FunSuite}


class PerformanceSuite extends FunSuite with BeforeAndAfter {
  var tokenizer: Tokenizer = null

  before {
    val lexiconDict = new LexiconDict().load()
    val connectionCostDict = new ConnectionCostDict().load()
    tokenizer = new Tokenizer(lexiconDict, connectionCostDict)
  }

  test("sentence") {
    val result = tokenizer.parseText("어제 앵꼬되었잖아. 버카충했어?").map(t => t.surface + ":" + t.feature(0)).mkString(",")
    println(result)
  }

  test("performance") {
    val times = 10000
    val startTime = System.nanoTime()
    for (i <- 0 until times) {
      tokenizer.parseText("안녕하세요. 형태소분석기입니다. 서울에서 살고있습니다.")
    }
    val endTime = System.nanoTime()
    val elapsedTime = (endTime - startTime) / times
    println(elapsedTime)
    println(s"$elapsedTime ns")
  }

  test("performance long term") {
    val times = 10000
    val startTime = System.nanoTime()
    for (i <- 0 until times) {
      tokenizer.parseText("안녕하세요형태소분석기입니다서울에서살고있습니다.")
    }
    val endTime = System.nanoTime()
    val elapsedTime = (endTime - startTime) / times
    println(elapsedTime)
    println(s"$elapsedTime ns")
  }


}
