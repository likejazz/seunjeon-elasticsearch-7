package org.bitbucket.eunjeon.seunjeon

import org.bitbucket.eunjeon.seunjeon.Pos.Pos

object SpacePenalty {
  val leftSpacePenaltyCost = Map(
    Pos.E -> 3000,
    Pos.EP -> 3000,
    Pos.J -> 6000,
    Pos.VCP -> 3000,
    Pos.XS -> 3000,
    Pos.XS -> 3000,
    Pos.XS -> 3000)

  def apply(pos: Pos) = {
    leftSpacePenaltyCost.getOrElse(pos, 0)
  }
}
