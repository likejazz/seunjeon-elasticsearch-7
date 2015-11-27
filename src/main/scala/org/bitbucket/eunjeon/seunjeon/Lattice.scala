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

import scala.collection.mutable


/**
  * Lattice 노드
  * @param morpheme   Morpheme
  * @param startPos  시작 offset
  * @param endPos   끝 offset
  * @param accumulatedCost  누적비용
  */
case class LatticeNode(morpheme:Morpheme, startPos:Int, endPos:Int, var accumulatedCost:Int = 9999) {
  var leftNode:LatticeNode = null

  // TODO: hashCode 랑 equals 구현안해도 Set에 중복없이 잘 들어가나?
  override def equals(o: Any) = o match {
    case that:LatticeNode => (that.startPos == startPos) && (that.endPos == endPos)
    case _ => false
  }

  override def hashCode = startPos.hashCode()<<20 + endPos.hashCode()
}

object Lattice {
  def apply(length:Int, connectingCostDict:ConnectionCostDict) = new Lattice(length, connectingCostDict)
}

// TODO: connectionCostDict 클래스로 빼자
class Lattice(length:Int, connectingCostDict:ConnectionCostDict) {
  var startingNodes = build2DimNodes(length+2)  // for BOS + EOS
  var endingNodes = build2DimNodes(length+2)    // for BOS + EOS
  var bos = new LatticeNode(new Morpheme("BOS", 0, 0, 0, IndexedSeq("BOS"), Pos.BOS), 0, 0, 0)
  var eos = new LatticeNode(new Morpheme("EOS", 0, 0, 0, IndexedSeq("EOS"), Pos.EOS), length, length)
  startingNodes.head += bos
  endingNodes.head += bos
  startingNodes.last += eos
  endingNodes.last += eos

  private def build2DimNodes(length:Int) : mutable.ArraySeq[mutable.MutableList[LatticeNode]] = {
    // TODO: immutable 로 바꿔서 성능향상시키자.
    val temp = new mutable.ArraySeq(length)
    temp.map(l => new mutable.MutableList[LatticeNode])
  }

  def add(latticeNode:LatticeNode): Lattice = {
    startingNodes(latticeNode.startPos+1) += latticeNode
    endingNodes(latticeNode.endPos+1) += latticeNode
    this
  }

  def addAll(latticeNodes:Seq[LatticeNode]): Lattice = {
    latticeNodes.foreach(node => add(node))
    this
  }

  def removeSpace(): Lattice = {
    startingNodes = startingNodes.filter(termNodes =>
      termNodes.isEmpty || termNodes.get(0).get.morpheme.surface != " ")
    endingNodes = endingNodes.filter(termNodes =>
      termNodes.isEmpty || termNodes.get(0).get.morpheme.surface != " ")
    this
  }

  // FIXME: space 패널티 cost 계산해줘야 함.
  def getBestPath: Seq[LatticeNode] = {
    for (idx <- 1 until startingNodes.length) {
      startingNodes(idx).foreach(updateCost(endingNodes(idx-1), _))
    }

    var result = new mutable.ListBuffer[LatticeNode]
    var node = eos
    while (node != null) {
      result += node
      node = node.leftNode
    }
    result.reverse
  }


  private def updateCost(endingNodes:Seq[LatticeNode], startingNode:LatticeNode): Unit = {
    var minTotalCost:Int = 2147483647
    endingNodes.foreach{ endingNode =>
      val totalCost: Int = getCost(endingNode, startingNode)
      if (totalCost < minTotalCost) {
        minTotalCost = totalCost
        startingNode.accumulatedCost = totalCost
        startingNode.leftNode = endingNode
      }
    }
  }

  private def getCost(endingNode: LatticeNode, startingNode: LatticeNode): Int = {
    val penaltyCost = if (endingNode.endPos + 1 != startingNode.startPos) SpacePenalty(startingNode.morpheme.pos) else 0

    endingNode.accumulatedCost +
      endingNode.morpheme.cost +
      connectingCostDict.getCost(endingNode.morpheme.rightId, startingNode.morpheme.leftId) +
      penaltyCost
  }
}

