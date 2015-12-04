package org.bitbucket.eunjeon.seunjeon

import scala.collection.mutable

case class Eojeol(nodes:Seq[LNode]) {
  val surface = nodes.map(_.morpheme.surface).mkString
  val startPos = nodes.head.startPos
  val endPos = nodes.last.endPos
}

object Eojeoler {

  def build(nodes:Seq[LNode]):Seq[Eojeol] =  {

    var result = mutable.LinearSeq[Eojeol]()

    val iter = nodes.iterator
    var pre = iter.next
    // TODO: Eojeol class 만들어서 surface, position 정보들을 담아볼까?
    var eojeolNodes = mutable.LinearSeq[LNode](pre)
    while (iter.hasNext) {
      val cur = iter.next
      if (appendable.contains(pre.morpheme.poses.last -> cur.morpheme.poses.head)) {
        eojeolNodes = eojeolNodes :+ cur
      }
      else {
        result = result :+ Eojeol(eojeolNodes)
        eojeolNodes = mutable.LinearSeq[LNode](cur)
      }
      pre = cur
    }
    result :+ Eojeol(eojeolNodes)
  }

  val appendable = Seq(

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
