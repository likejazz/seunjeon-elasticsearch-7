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

import scala.io.Source


object ConnectionCostDict {
  val resourceConnDicFile = "/connection_cost.dat"
}

class ConnectionCostDict {
  var costDict: Array[Int] = null

  def loadFromString(str: String): Unit = {
    val iter = str.stripMargin.split("\n").toIterator
    loadFromIterator(iter)
  }
  
  def loadFromFile(file: String): Unit = {
    val lines: Iterator[String] = Source.fromFile(file, "utf-8").getLines()
    loadFromIterator(lines)
  }

  def loadFromIterator(costDictIter: Iterator[String]): Unit = {
    val costs_file = costDictIter.toSeq
    val sizes = costs_file.head.split(' ').map(v => v.toShort)
    val rightSize = sizes(0)
    val leftSize = sizes(1)
    costDict = new Array[Int]((rightSize+1) * (leftSize+1))
    val costs = costs_file.tail.foreach { line =>
      val v = line.split(' ').map(_.toShort)
      val rightId = v(0)
      val leftId = v(1)
      val cost = v(2)
      costDict(rightId*leftId + leftId) = cost
    }
  }

  def getCost(rightId: Short, leftId: Short ): Int = {
    costDict(rightId*leftId + leftId)
  }

  def save(path: String): Unit = {
    val store = new ObjectOutputStream(
      new BufferedOutputStream(
        new FileOutputStream(path), 1024*16))
    store.writeObject(costDict)
    store.close()

  }

  def load(): ConnectionCostDict = {
    val inputStream = getClass.getResourceAsStream(ConnectionCostDict.resourceConnDicFile)
    load(inputStream)
    this
  }

  private def load(inputStream: InputStream): Unit = {
    val in = new ObjectInputStream(
      new BufferedInputStream(inputStream, 1024*16))
    costDict = in.readObject().asInstanceOf[Array[Int]]
    in.close()
  }

}
