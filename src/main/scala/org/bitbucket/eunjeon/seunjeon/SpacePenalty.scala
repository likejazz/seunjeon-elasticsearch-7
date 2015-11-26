package org.bitbucket.eunjeon.seunjeon

import org.bitbucket.eunjeon.seunjeon.Pos.Pos

object SpacePenalty {
  val leftSpacePenaltyCost = Map(
    Pos.EC -> 3000,
    Pos.EF -> 3000,
    Pos.EP -> 3000,
    Pos.ETM -> 3000,
    Pos.ETN -> 3000,
    Pos.JC -> 6000,
    Pos.JKB -> 6000,
    Pos.JKC -> 6000,
    Pos.JKG -> 6000,
    Pos.JKO -> 6000,
    Pos.JKQ -> 6000,
    Pos.JKS -> 6000,
    Pos.JKV -> 6000,
    Pos.JX -> 6000,
    Pos.VCP -> 3000,
    Pos.XSA -> 3000,
    Pos.XSN -> 3000,
    Pos.XSV -> 3000,
    Pos.INFLECT_EC -> 3000,
    Pos.INFLECT_EF -> 3000,
    Pos.INFLECT_EP -> 3000,
    Pos.INFLECT_ETM -> 3000,
    Pos.INFLECT_ETN -> 3000,
    Pos.INFLECT_JC -> 6000,
    Pos.INFLECT_JKB -> 6000,
    Pos.INFLECT_JKC -> 6000,
    Pos.INFLECT_JKG -> 6000,
    Pos.INFLECT_JKO -> 6000,
    Pos.INFLECT_JKQ -> 6000,
    Pos.INFLECT_JKS -> 6000,
    Pos.INFLECT_JKV -> 6000,
    Pos.INFLECT_JX -> 6000,
    Pos.INFLECT_XSA -> 3000,
    Pos.INFLECT_XSN -> 3000,
    Pos.INFLECT_XSV -> 3000,
    Pos.INFLECT_VCP -> 3000)

  def apply(pos: Pos) = leftSpacePenaltyCost.getOrElse(pos, 0)
}
