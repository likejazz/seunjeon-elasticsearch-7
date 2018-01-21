package org.bitbucket.eunjeon.seunjeon

import scala.io.Source


object UnkDef {
  val (default: Morpheme, unknowns: Map[String, Morpheme]) = buildUnk

  def buildUnk: (Morpheme, Map[String, Morpheme]) = {
    val unkMorphemes: Seq[Morpheme] =
      Source.fromInputStream(classOf[CharSet].getResourceAsStream(DictBuilder.UNK_DEF), "UTF-8").
        getLines().
        map(line2Morpheme).
        toSeq

    val (default, others) = unkMorphemes.partition(_.getSurface == "DEFAULT")
    (default.head, others.map(x => x.getSurface -> x).toMap)
  }

  private def line2Morpheme(str: String) = {
    val arr = str.split(",")
    val feature = arr.slice(4, arr.length)
    UnkMorpheme(
      arr(0),
      arr(1).toShort,
      arr(2).toShort,
      arr(3).toShort,
      feature.mkString(","),
      MorphemeType(feature),
      Pos.poses(feature))
  }

  def apply(name: String): Option[Morpheme] = unknowns.get(name)
}

