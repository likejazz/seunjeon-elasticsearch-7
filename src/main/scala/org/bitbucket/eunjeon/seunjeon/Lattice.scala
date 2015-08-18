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
 * Created by parallels on 7/28/15.
 */

case class LatticeNode(term:Term, var accumulatedCost:Int = 9999) {
  var leftNode:LatticeNode = null
}

// TODO: connectionCostDict 클래스로 빼자
class Lattice(length:Int, connectingCostDict:ConnectionCostDict) {
  var startingNodes = build2DimNodes(length+2)  // for BOS + EOS
  var endingNodes = build2DimNodes(length+2)    // for BOS + EOS
  var bos = new LatticeNode(new Term("BOS", 0, 0, 0, "BOS"), 0)
  var eos = new LatticeNode(new Term("EOS", 0, 0, 0, "EOS"))
  startingNodes.head += bos
  endingNodes.head += bos
  startingNodes.last += eos
  endingNodes.last += eos

  private def build2DimNodes(length:Int)
  : mutable.ArraySeq[mutable.MutableList[LatticeNode]] = {
    val temp = new mutable.ArraySeq(length)
    temp.map(l => new mutable.MutableList[LatticeNode])
  }


  def add(term: Term, startPos:Int, endPos:Int): Unit = {
    val latticeNode = new LatticeNode(term)
    startingNodes(startPos+1) += latticeNode
    endingNodes(endPos+1) += latticeNode
  }

//  def addUnknownWords(sentence: String): Unit = {
//    for ((nodes, i) <- sentence.view.zipWithIndex) {
//      this.add(Term.createUnknownTerm(sentence.substring(i, i+1)), i, i)
//    }
//  }

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


  private def updateCost(endingNodes:Seq[LatticeNode], startingNode:LatticeNode): Unit = {
    var minTotalCost:Int = 99999999
    endingNodes.foreach{ endingNode =>
      val connectingCost:Int = if (endingNode.term.rightId == -1 || startingNode.term.leftId == -1) {
        1000
      } else {
        connectingCostDict.getCost(endingNode.term.rightId, startingNode.term.leftId)
      }
      val totalCost = endingNode.accumulatedCost + endingNode.term.cost + connectingCost
      if (totalCost < minTotalCost) {
        minTotalCost = totalCost
        startingNode.accumulatedCost = totalCost
        startingNode.leftNode = endingNode
      }
    }
  }

}

