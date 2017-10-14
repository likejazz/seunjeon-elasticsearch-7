package org.bitbucket.eunjeon.seunjeon

import scala.io.Source

object NngUtil {

  var nngLeftId = getNngLeftId()
  var nngRightId: Short = 0
  var nngTRightId: Short = 0
  var nngFRightId: Short = 0

  def getNngLeftId(dicPath:String = DictBuilder.LEFT_ID_DEF):Short = {
    val leftIdDefStream = classOf[NngUtil].getResourceAsStream(dicPath)
    Source.fromInputStream(leftIdDefStream, "UTF-8").getLines().
      map(_.split(" ")).
      filter(_(1).startsWith("NNG,*,")).
      map(_(0).toShort).toSeq.head
  }

  val rightIdDefStream = classOf[NngUtil].getResourceAsStream(DictBuilder.RIGHT_ID_DEF)
  Source.fromInputStream(rightIdDefStream, "UTF-8").getLines().foreach { line =>
    val idFeature = line.split(" ")
    val feature = idFeature(1)
    if (feature.startsWith("NNG,*,T,")) {
      nngTRightId = idFeature(0).toShort
    } else if (feature.startsWith("NNG,*,F,")) {
      nngFRightId = idFeature(0).toShort
    } else if (feature.startsWith("NNG,*,*,")) {
      nngRightId = idFeature(0).toShort
    }
  }
}

class NngUtil

