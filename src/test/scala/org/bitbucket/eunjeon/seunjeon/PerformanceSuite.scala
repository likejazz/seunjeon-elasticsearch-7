/**
 * Copyright 2015 youngho yu, yongwoon lee
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
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

  ignore("performance long term") {
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

  test("performance from file") {
    println(tokenizer.parseText("dic loading"))
    val source = scala.io.Source.fromFile("./src/test/resources/outofmemory.txt")
    val lines = try source.mkString finally source.close()

    val times = 1000
    val startTime = System.nanoTime()
    for (i <- 0 until times) {
      tokenizer.parseText(lines)
    }
    val endTime = System.nanoTime()
    val elapsedTime = (endTime - startTime) / times
    println(elapsedTime)
    println(s"$elapsedTime us")
  }

}
