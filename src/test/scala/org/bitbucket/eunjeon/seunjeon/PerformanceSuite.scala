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

  test("performance long term") {
    var result:Seq[LNode] = null
    val times = 100
    val startTime = System.nanoTime()
    for (i <- 0 until times) {
      result = tokenizer.parseText("안녕하세요형태소분석기입니다.서울에서살고있습니다.")
    }
    val endTime = System.nanoTime()
    val elapsedTime = (endTime - startTime) / times
    result.foreach(println)
    println(elapsedTime)
    println(s"$elapsedTime ns")
  }

  ignore("performance too_many_special_chars") {
    filetest("./src/test/resources/too_many_special_chars.txt")
  }

  test("performance long_sentence") {
    filetest("./src/test/resources/long_sentence.txt")
  }

  def filetest(path:String): Unit = {
    println(tokenizer.parseText("dic loading"))
    val source = scala.io.Source.fromFile(path)
    val lines = try source.mkString finally source.close()

    val times = 100
    val startTime = System.nanoTime()
    var result:Seq[LNode] = null
    for (i <- 0 until times) {
      result = tokenizer.parseText(lines)
    }
    val endTime = System.nanoTime()
    val elapsedTime = (endTime - startTime) / times
    result.foreach(println)
    println(s"$elapsedTime us")
  }
}
