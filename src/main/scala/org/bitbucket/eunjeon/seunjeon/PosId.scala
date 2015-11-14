package org.bitbucket.eunjeon.seunjeon

import scala.io.Source

/**
  * Created by parallels on 11/11/15.
  */

case class Rule(pattern:Seq[String], posid:Int)

object PosId {
  val rules = buildRules()

  private def buildRules(): Seq[Rule] = {
    val inputStream = getClass.getResourceAsStream(DictBuilder.POS_ID_DEF)
    Source.fromInputStream(inputStream).getLines().map { line =>
      val patternPosId = line.split(" ")
      Rule(patternPosId(0).split(","), patternPosId(1).toInt)
    }.toSeq
  }

  def apply(feature:Seq[String]): Int = {
    try {
      rules.find(rule => isMatch(rule.pattern, feature)).get.posid
    } catch {
      case _: Throwable => println(feature); throw new Exception
    }
  }


  private def isMatch(pattern:Seq[String], feature:Seq[String]): Boolean = {
    for (idx <- pattern.indices) {
      val patternChar = pattern(idx)
      if (patternChar != "*" && patternChar != feature(idx)) return false
    }
    true
  }

}
