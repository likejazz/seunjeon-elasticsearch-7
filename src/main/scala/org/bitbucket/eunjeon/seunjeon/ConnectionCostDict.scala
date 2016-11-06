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

import java.io._

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import scala.io.Source


class ConnectionCostDict {
  val logger = Logger(LoggerFactory.getLogger(classOf[ConnectionCostDict].getName))

  var costDict: Array[Int] = null
  var rightSize = 0
  var leftSize = 0

  def loadFromString(str: String): Unit = {
    val iter = str.stripMargin.split("\n").toIterator
    loadFromIterator(iter)
  }
  
  def loadFromFile(file: String): Unit = {
    val lines: Iterator[String] = Source.fromFile(file, "utf-8").getLines()
    loadFromIterator(lines)
  }

  def loadFromIterator(costDictIter: Iterator[String]): Unit = {
    val startTime = System.nanoTime()
    /*
    text cost dictionary info
    3819 2694     : rightSize, leftSize (header)
    1 1 0         : rightId, leftId, cost (rightId/leftId is from 0 to size - 1)
    1 0 0
    ...
    3818 2689 864
    3818 2690 863
    3818 2691 864
    3818 2692 864
    3818 2693 569
     */
    val costs_file = costDictIter.toSeq
    val sizes = costs_file.head.split(' ').map(v => v.toShort)
    rightSize = sizes(0)
    leftSize = sizes(1)
    //2 is to save rightSize & leftSize
    costDict = new Array[Int](rightSize * leftSize + 2)
    costDict(costDict.length - 2) = rightSize
    costDict(costDict.length - 1) = leftSize
    val costs = costs_file.tail.foreach { line =>
      val v = line.split(' ').map(_.toShort)
      val rightId = v(0)
      val leftId = v(1)
      val cost = v(2)
      costDict(rightId*leftSize + leftId) = cost
    }

    def elapsedTime = (System.nanoTime() - startTime) / (1000*1000)
    logger.info(s"connectionDict loading is completed. ($elapsedTime ms)")
  }

  def getCost(rightId: Short, leftId: Short ): Int = {
    costDict(rightId*leftSize + leftId)
  }

  def save(path: String): Unit = {
    val store = new ObjectOutputStream(
      new BufferedOutputStream(
        new FileOutputStream(path), 1024 * 16))
    store.writeObject(costDict)
    store.close()

  }

  def load(): ConnectionCostDict = {
    val connectionCostFile = DictBuilder.DICT_PATH + DictBuilder.CONNECTION_COST_FILENAME
    val inputStream = classOf[ConnectionCostDict].getResourceAsStream(connectionCostFile)
    load(inputStream)
    this
  }

  private def load(inputStream: InputStream): Unit = {
    val in = new ObjectInputStream(
      new BufferedInputStream(inputStream, 1024 * 16))
    costDict = in.readObject().asInstanceOf[Array[Int]]
    rightSize = costDict(costDict.length - 2)
    leftSize = costDict(costDict.length - 1)
//    println(getDictionaryInfo())
    in.close()
  }

  def getDictionaryInfo(): String = {
    s"rightSize : $rightSize, leftSize : $leftSize, size : ${costDict.length}"
  }
}
