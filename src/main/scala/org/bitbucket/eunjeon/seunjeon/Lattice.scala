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
  def apply(text:String, connectingCostDict:ConnectionCostDict) = new Lattice(text, connectingCostDict)
}

class Lattice(input:String, connectingCostDict:ConnectionCostDict) {
  val text = input
  var startingNodes = build2DimNodes(text.length+2)  // for BOS + EOS
  var endingNodes = build2DimNodes(text.length+2)    // for BOS + EOS
  var bos = new LNode(BasicMorpheme("BOS", 0, 0, 0, "", MorphemeType.COMMON, Array(Pos.BOS)), 0, 0, 0)
  var eos = new LNode(BasicMorpheme("EOS", 0, 0, 0, "", MorphemeType.COMMON, Array(Pos.BOS)), text.length, text.length)
  startingNodes.head += bos
  endingNodes.head += bos
  startingNodes.last += eos
  endingNodes.last += eos

  private def build2DimNodes(length:Int) : mutable.ArraySeq[mutable.ArrayBuffer[LNode]] = {
    // TODO: immutable 로 바꿔서 성능향상시키자.
    val temp = new mutable.ArraySeq(length)
    temp.map(l => new mutable.ArrayBuffer[LNode])
  }

  def add(node:LNode): Lattice = {
    startingNodes(node.beginOffset+1) += node
    endingNodes(node.endOffset) += node
    this
  }

  def addAll(nodes:Seq[LNode]): Lattice = {
    nodes.foreach(node => add(node))
    this
  }

  def build(): Lattice = {
    this.fillDisconnectedPath().removeSpace()
  }

  def fillDisconnectedPath(): Lattice = {
    for (idx <- text.length-1 to 0 by -1) {
      val eIdx = idx + 1
      if (endingNodes(eIdx).isEmpty && startingNodes(eIdx + 1).nonEmpty) {
        val categoryMorpheme = CharSetDef.getCategoryTerm(text(eIdx))
        val morpheme = UnkMorpheme(text(idx).toString, categoryMorpheme._2)
        // TODO: idx 무지 헷깔림
        add(LNode(morpheme, idx, idx + 1))
      }
    }
    this
  }

  def removeSpace(): Lattice = {
    assert(startingNodes.length == endingNodes.length)
    startingNodes = startingNodes.filterNot(isSpace)
    endingNodes = endingNodes.filterNot(isSpace)
    assert(startingNodes.length == endingNodes.length)
    this
  }

  private def isSpace(nodes:mutable.ArrayBuffer[LNode]):Boolean = {
    nodes.length == 1 && nodes.exists(_.morpheme.getSurface == " ")
  }

  // FIXME: space 패널티 cost 계산해줘야 함.
  def getBestPath(offset:Int=0): Seq[LNode] = {
    for (idx <- 1 until startingNodes.length) {
      // while 하는 것이 foreach 보다 성능이 좋음.
      //  https://www.sumologic.com/2012/07/23/3-tips-for-writing-performant-scala/
      val iter = startingNodes(idx).iterator
      while (iter.hasNext) {
        // FIXME: endingNodes 가 없으면 startingNode를 지워줘야 할듯?
        updateCost(endingNodes(idx - 1), iter.next())
      }
    }

    var result = new mutable.ListBuffer[LNode]
    var node = eos
    while (node != null) {
      result += node
      node = node.leftNode
    }
    result.reverse.map(addOffset(offset, _))
  }

  def addOffset(offset: Int, node: LNode): LNode = {
    LNode(node.morpheme, node.beginOffset + offset, node.endOffset + offset, node.accumulatedCost)
  }

  private def updateCost(endingNodes:Seq[LNode], startingNode:LNode): Unit = {
    var minTotalCost:Int = Int.MaxValue
    // while 하는 것이 foreach 보다 성능이 좋음.
    //  https://www.sumologic.com/2012/07/23/3-tips-for-writing-performant-scala/
    var bestNode:LNode = null
    val iter = endingNodes.iterator
    while (iter.hasNext) {
      val endingNode = iter.next()
      val totalCost:Int = getCost(endingNode, startingNode)
      if (totalCost < minTotalCost) {
        minTotalCost = totalCost
        startingNode.accumulatedCost = totalCost
        bestNode = endingNode
      }
    }
    if (bestNode == null) {
      throw new Exception(s"disconnected path.\n endingNodes=$endingNodes\n startingNode=$startingNode")
    }
    startingNode.leftNode = bestNode
  }

  private def getCost(endingNode: LNode, startingNode: LNode): Int = {
    val penaltyCost =
      if (endingNode.endOffset != startingNode.beginOffset)
        SpacePenalty(startingNode.morpheme.getPoses(0))
      else 0

    endingNode.accumulatedCost +
      endingNode.morpheme.getCost +
      connectingCostDict.getCost(endingNode.morpheme.getRightId, startingNode.morpheme.getLeftId) +
      penaltyCost
  }
}

