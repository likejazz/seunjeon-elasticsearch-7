package org.bitbucket.eunjeon.seunjeon

import scala.collection.mutable

case class Eojeol(var nodes:Seq[LNode]) {
  val surface = nodes.map(_.morpheme.surface).mkString
  val startPos = nodes.head.startPos
  val endPos = nodes.last.endPos

  def deCompound(): Eojeol = {
    nodes = nodes.flatMap { node =>
      val deCompounded = LNode.deCompound(node).toList
      if (deCompounded.size > 1) {
        deCompounded.head :: node :: deCompounded.tail ::: Nil
      } else {
        deCompounded
      }
    }
    this
  }
}

object Eojeoler {
  def build(nodes:Seq[LNode], deCompound:Boolean):Seq[Eojeol] = {
    val result = mutable.ListBuffer[Eojeol]()
    var eojeolNodes = mutable.LinearSeq[LNode](nodes.head)
    nodes.sliding(2).foreach { slid =>
      val pre = slid.head
      val cur = slid.last
      // TODO: contains 더 빠르게 자료구조 바꿔야 함.
      if (appendable.contains(pre.morpheme.poses.last -> cur.morpheme.poses.head)) {
        eojeolNodes = eojeolNodes :+ cur
      } else {
        result.append(Eojeol(eojeolNodes))
        eojeolNodes = mutable.LinearSeq[LNode](cur)
      }
    }
    // TODO: mutable 에 직접 넣는거 찾아보자.. 새로운 list 리턴 안하는...
    result.append(Eojeol(eojeolNodes))
    if (deCompound) {
      result.map(_.deCompound())
    } else {
      result
    }
  }

  val appendable = Set(
    Pos.V -> Pos.EP,

    Pos.EP -> Pos.E,
    Pos.E -> Pos.E,
    Pos.XR -> Pos.E,
    Pos.V -> Pos.E,
    Pos.XS -> Pos.E,

    Pos.N -> Pos.XS,
    Pos.M -> Pos.XS,
    Pos.XR -> Pos.XS,
    Pos.UNKNOWN -> Pos.XS,

    Pos.N -> Pos.VCP,
    Pos.XS -> Pos.VCP,
    Pos.UNKNOWN -> Pos.VCP,

    Pos.N -> Pos.J,
    Pos.XS -> Pos.J,
    Pos.E -> Pos.J,
    Pos.M -> Pos.J,
    Pos.J -> Pos.J,
    Pos.SL -> Pos.J,
    Pos.SH -> Pos.J,
    Pos.SN -> Pos.J,
    Pos.UNKNOWN -> Pos.J,

    Pos.XP -> Pos.N
  )

}
