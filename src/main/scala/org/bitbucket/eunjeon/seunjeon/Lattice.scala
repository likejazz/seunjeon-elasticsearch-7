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

object Lattice {
  def apply(length:Int, connectingCostDict:ConnectionCostDict) = new Lattice(length, connectingCostDict)
}

// TODO: connectionCostDict 클래스로 빼자
class Lattice(length:Int, connectingCostDict:ConnectionCostDict) {
  var startingNodes = build2DimNodes(length+2)  // for BOS + EOS
  var endingNodes = build2DimNodes(length+2)    // for BOS + EOS
  var bos = new LNode(new Morpheme("BOS", 0, 0, 0, Array("BOS"), MorphemeType.GENERAL, Array(Pos.BOS)), 0, 0, 0)
  var eos = new LNode(new Morpheme("EOS", 0, 0, 0, Array("EOS"), MorphemeType.GENERAL, Array(Pos.BOS)), length, length)
  startingNodes.head += bos
  endingNodes.head += bos
  startingNodes.last += eos
  endingNodes.last += eos

  private def build2DimNodes(length:Int) : mutable.ArraySeq[mutable.MutableList[LNode]] = {
    // TODO: immutable 로 바꿔서 성능향상시키자.
    val temp = new mutable.ArraySeq(length)
    temp.map(l => new mutable.MutableList[LNode])
  }

  def add(node:LNode): Lattice = {
    startingNodes(node.startPos+1) += node
    endingNodes(node.endPos) += node
    this
  }

  def addAll(nodes:Seq[LNode]): Lattice = {
    nodes.foreach(node => add(node))
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
  def getBestPath(): Seq[LNode] = {
    for (idx <- 1 until startingNodes.length) {
      startingNodes(idx).foreach(updateCost(endingNodes(idx-1), _))
    }

    var result = new mutable.ListBuffer[LNode]
    var node = eos
    while (node != null) {
      result += node
      node = node.leftNode
    }
    result.reverse
  }

  private def updateCost(endingNodes:Seq[LNode], startingNode:LNode): Unit = {
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

  private def getCost(endingNode: LNode, startingNode: LNode): Int = {
    val penaltyCost = if (endingNode.endPos != startingNode.startPos) {
      SpacePenalty(startingNode.morpheme.poses(0))
    } else 0

    endingNode.accumulatedCost +
      endingNode.morpheme.cost +
      connectingCostDict.getCost(endingNode.morpheme.rightId, startingNode.morpheme.leftId) +
      penaltyCost
  }
}

