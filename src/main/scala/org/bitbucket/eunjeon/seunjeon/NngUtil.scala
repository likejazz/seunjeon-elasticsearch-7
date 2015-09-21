package org.bitbucket.eunjeon.seunjeon

import scala.io.Source

object NngUtil {
  var nngLeftId = 0
  var nngTRightId = 0
  var nngFRightId = 0

  val leftIdDefStream = getClass.getResourceAsStream(DictBuilder.LEFT_ID_DEF)
  Source.fromInputStream(leftIdDefStream).getLines().foreach { line =>
    val idFeature = line.split(" ")
    val feature = idFeature(1)
    if (feature.startsWith("NNG,*,")) {
      nngLeftId = idFeature(0).toShort
    }
  }

  val rightIdDefStream = getClass.getResourceAsStream(DictBuilder.RIGHT_ID_DEF)
  Source.fromInputStream(rightIdDefStream).getLines().foreach { line =>
    val idFeature = line.split(" ")
    val feature = idFeature(1)
    if (feature.startsWith("NNG,*,T,")) {
      nngTRightId = idFeature(0).toShort
    } else if (feature.startsWith("NNG,*,F,")) {
      nngFRightId = idFeature(0).toShort
    }
  }

}
