package org.bitbucket.eunjeon.seunjeon

import org.scalatest.FunSuite


class ConnectionCostDictTest extends FunSuite {

  test("ConnectionCost load performance") {
    {
      val startTime = System.nanoTime()
      val connectionCostDict = new ConnectionCostDict
      connectionCostDict.loadFromFile("/home/parallels/Downloads/mecab-ko-dic-1.6.1-20140814/matrix.def")
      val endTime = System.nanoTime()
      val elapsedTime = (endTime - startTime) / 1000000
      println(s"$elapsedTime ms")
      connectionCostDict.save("src/main/resources/connection_cost.dat")
    }
    {
      val startTime = System.nanoTime()
      val connectionCostDict = new ConnectionCostDict
      connectionCostDict.open()
      val endTime = System.nanoTime()
      val elapsedTime = (endTime - startTime) / 1000000
      println(s"$elapsedTime ms")
      assert(0 == connectionCostDict.getCost(1, 1))
    }
  }

}
