package org.bitbucket.eunjeon.seunjeon

import scala.io.Source

import scala.collection.mutable

/**
  * Created by parallels on 11/11/15.
  */
object Dicrc {
  val leftSpacePenaltyFactors = getLeftSpacePenaltyFactors()

  private def getLeftSpacePenaltyFactors(): mutable.HashMap[Int, Int] = {
    val inputStream = getClass.getResourceAsStream(DictBuilder.DICRC)
    val values = Source.fromInputStream(inputStream).getLines().
      map(_.split("=").map(_.trim)).
      filter(_(0) == "left-space-penalty-factor").toSeq.head.
      last. // values
      split(",")

      val valuesIter = values.iterator
      val penaltyCosts = new mutable.HashMap[Int, Int]
      while (valuesIter.hasNext) {
        val id = valuesIter.next.toInt
        val cost = valuesIter.next.toInt
        penaltyCosts.put(id, cost)
      }
      penaltyCosts
  }

  def getPenaltyCost(posid: Int) = leftSpacePenaltyFactors.getOrElse(posid, 0)

}
