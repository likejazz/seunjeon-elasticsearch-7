package org.bitbucket.eunjeon.seunjeon

import scala.io.Source

/**
  * Created by parallels on 11/11/15.
  */
object Dicrc {
  val MAX_POS_ID = 999
  // TODO: pos-id.def 에서 읽어오자.
  val UNKNOWN_POS_ID = MAX_POS_ID
  val leftSpacePenaltyFactors = getLeftSpacePenaltyFactors()

  private def getLeftSpacePenaltyFactors(): Array[Int] = {
    val inputStream = getClass.getResourceAsStream(DictBuilder.DICRC)
    val values = Source.fromInputStream(inputStream).getLines().
      map(_.split("=").map(_.trim)).
      filter(_(0) == "left-space-penalty-factor").toSeq.head.
      last. // values
      split(",")

      val valuesIter = values.iterator
      val penaltyCosts = Array.fill(MAX_POS_ID + 1){0}
      while (valuesIter.hasNext) {
        val id = valuesIter.next.toInt
        val cost = valuesIter.next.toInt
        penaltyCosts.update(id, cost)
      }
      penaltyCosts
  }

  def getPenaltyCost(posid: Int) = leftSpacePenaltyFactors(posid)
}
