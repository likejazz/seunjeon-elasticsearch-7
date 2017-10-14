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


object Benchmark {
  def apply(n: Int, ignoredTimes:Int)(op: => Any): Unit = {
    val measureTimes = n-ignoredTimes
    var min = Long.MaxValue
    val avg = (1 to n).map { i =>
      val start = System.nanoTime
      op
      val end = System.nanoTime
      val elapsed = (end - start) / 1000 / 1000
      println(s"$i: $elapsed ms")
      if (elapsed < min) {
        min = elapsed
      }
      elapsed
    }.drop(ignoredTimes).sum / measureTimes
    println(s"measureTimes: $measureTimes, average: $avg ms, min: $min ms")
  }
}

object stopwatch {
  def apply[T](block: => T): (T, Long) = {
    val start = System.nanoTime()
    val ret = block
    val end = System.nanoTime()
    (ret, (end - start) / 1000000)
  }
}

class PerformanceSuite extends FunSuite with BeforeAndAfter {
  ignore("long sentence") {
    val source = scala.io.Source.fromFile("./src/test/resources/too_many_special_chars.txt")
    val lines = try source.mkString finally source.close()
    Benchmark(1, 0)(Analyzer.parse(lines.mkString(" ")))  // dict loading time
    Benchmark(100, 20)(Analyzer.parse(lines.mkString(" ")))
  }
}
