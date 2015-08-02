import scala.io.Source

/**
 * Created by parallels on 8/2/15.
 */
class ConnectionCostDict {
  var costDict: Array[Array[Int]] = null//Array.ofDim[Short](1, 2)

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
    costDict = Array.ofDim[Int](sizes(0), sizes(1))
    val costs = costs_file.tail.map(_.split(' ').map(_.toShort)).
      foreach(v => costDict(v(0))(v(1)) = v(2))
  }

  def getCost(rightId: Short, leftId: Short ): Int = {
    costDict(rightId)(leftId)
  }
}
