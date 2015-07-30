import scala.collection.mutable

/**
 * Created by parallels on 7/28/15.
 */

case class LatticeNode(term:Term, var accumulatedCost:Int = 9999) {
  var leftNode:LatticeNode = null
}

class Lattice(length:Int, connectingCostDict:Array[Array[Short]]) {
  var startingNodes = build2DimNodes(length+2)  // for BOS + EOS
  var endingNodes = build2DimNodes(length+2)    // for BOS + EOS
  var bos = new LatticeNode(new Term("BOS", 0, 0, 0, null), 0)
  var eos = new LatticeNode(new Term("EOS", 0, 0, 0, null))
  startingNodes.head += bos
  endingNodes.head += bos
  startingNodes.last += eos
  endingNodes.last += eos

  def add(term: Term, startPos:Int, endPos:Int): Unit = {
    val latticeNode = new LatticeNode(term)
    startingNodes(startPos+1) += latticeNode
    endingNodes(endPos+1) += latticeNode
  }

  private def build2DimNodes(length:Int) = {
    val temp = new mutable.ArraySeq(length)
    temp.map(l => new mutable.MutableList[LatticeNode])
  }

  def getBestPath: Seq[Term] = {
    for (idx <- 1 until startingNodes.length) {
      startingNodes(idx).foreach{ startingNode:LatticeNode =>
        updateCost(endingNodes(idx-1), startingNode)
      }
    }

    var result = new mutable.ListBuffer[Term]
    var node = eos
    while (node != null) {
      result += node.term
      node = node.leftNode
    }
    result.reverse
  }


  def updateCost(endingNodes:Seq[LatticeNode], startingNode:LatticeNode): Unit = {
    var minTotalCost:Int = 9999
    endingNodes.foreach{ endingNode =>
      val connectingCost:Short = connectingCostDict(endingNode.term.rightId)(startingNode.term.leftId)
      val totalCost = endingNode.accumulatedCost + endingNode.term.cost + connectingCost
      if (totalCost < minTotalCost) {
        minTotalCost = totalCost
        startingNode.accumulatedCost = totalCost
        startingNode.leftNode = endingNode
      }
    }
  }

}

