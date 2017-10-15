package org.bitbucket.eunjeon.seunjeon

import org.bitbucket.eunjeon.seunjeon.Pos.Pos

object SpacePenalty {
  val leftSpacePenaltyCost: Array[Int] = {
    val penalties = new Array[Int](Pos.maxId)
    penalties.update(Pos.E.id, 3000)
    penalties.update(Pos.EP.id, 3000)
    penalties.update(Pos.J.id, 3000)
    penalties.update(Pos.VCP.id, 3000)
    penalties.update(Pos.XS.id, 3000)

    penalties
  }

  def apply(pos: Pos):Int = {
    leftSpacePenaltyCost(pos.id)
  }
}
