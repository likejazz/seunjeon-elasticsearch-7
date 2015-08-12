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
