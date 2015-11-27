package org.bitbucket.eunjeon.seunjeon


case class Eojeol(nodes:Seq[LatticeNode])

object Eojeoler {

  def build(nodes:Seq[LatticeNode]): Seq[Eojeol] = {
    nodes.foreach { node =>

    }
  }

  val appendable = Map(
    Pos.NNG -> Pos.JC,
    Pos.NNG -> Pos.JX
  )

}
