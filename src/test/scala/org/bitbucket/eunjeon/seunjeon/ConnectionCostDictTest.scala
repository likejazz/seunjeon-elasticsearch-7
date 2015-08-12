package org.bitbucket.eunjeon.seunjeon

import org.scalatest.FunSuite


class ConnectionCostDictTest extends FunSuite {

  ignore("ConnectionCost load performance") {
    {
      val startTime = System.nanoTime()
      val connectionCostDict = new ConnectionCostDict
      connectionCostDict.loadFromFile("mecab-ko-dic/matrix.def")
      val endTime = System.nanoTime()
      val elapsedTime = (endTime - startTime) / 1000000
      println(s"$elapsedTime ms")
      connectionCostDict.save("src/test/resources/connection_cost.dat")
    }
    {
      val startTime = System.nanoTime()
      val connectionCostDict = new ConnectionCostDict
      connectionCostDict.load()
      val endTime = System.nanoTime()
      val elapsedTime = (endTime - startTime) / 1000000
      println(s"$elapsedTime ms")
      assert(0 == connectionCostDict.getCost(1, 1))
    }
  }

}
