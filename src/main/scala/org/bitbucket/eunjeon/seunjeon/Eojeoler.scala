package org.bitbucket.eunjeon.seunjeon


import scala.collection.mutable


object Eojeoler {

  def build(nodes:Seq[LNode]): Seq[Eojeol] = {
    if (nodes.isEmpty) {
      Seq()
    } else if (nodes.length == 1) {
      Seq(Eojeol(nodes))
    } else {
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
      result
    }
  }

  val appendable = Set(
    Pos.XS -> Pos.EP,
    Pos.V -> Pos.EP,

    Pos.EP -> Pos.E,
    Pos.E -> Pos.E,
    Pos.XR -> Pos.E,
    Pos.V -> Pos.E,
    Pos.XS -> Pos.E,

    Pos.N -> Pos.XS,
    Pos.M -> Pos.XS,
    Pos.XR -> Pos.XS,
    Pos.UNK -> Pos.XS,

    Pos.N -> Pos.VCP,
    Pos.XS -> Pos.VCP,
    Pos.UNK -> Pos.VCP,

    Pos.N -> Pos.J,
    Pos.XS -> Pos.J,
    Pos.E -> Pos.J,
    Pos.M -> Pos.J,
    Pos.J -> Pos.J,
    Pos.SL -> Pos.J,
    Pos.SH -> Pos.J,
    Pos.SN -> Pos.J,
    Pos.UNK -> Pos.J,

    Pos.XP -> Pos.N
  )

}
