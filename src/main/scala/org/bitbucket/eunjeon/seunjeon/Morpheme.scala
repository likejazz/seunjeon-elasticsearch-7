package org.bitbucket.eunjeon.seunjeon

import java.io.{ObjectInputStream, ObjectOutputStream, IOException}

import org.bitbucket.eunjeon.seunjeon.Pos.Pos

import scala.collection.mutable


object Morpheme {
  def createUnknownMorpheme(surface:String, morpheme: Morpheme): Morpheme = {
    new Morpheme(surface,
      morpheme.leftId,
      morpheme.rightId,
      morpheme.cost*surface.length,
      morpheme.feature,
      wrapRefArray(Array(Pos.UNKNOWN)))
  }
}

/**
  * 형태소
  * @param surface  표현층
  * @param leftId   좌문맥ID
  * @param rightId  우문맥ID
  * @param cost     Term 비용
  * @param feature  feature
  * @param poses    품사  [[https://bitbucket.org/eunjeon/mecab-ko-dic/src/5fad4609d23a1b172a57e23addfe167ac5f02bf1/seed/pos-id.def?at=master&fileviewer=file-view-default]]
  */
case class Morpheme(var surface:String,
                    var leftId:Short,
                    var rightId:Short,
                    var cost:Int,
                    var feature:mutable.WrappedArray[String],
                    var poses:mutable.WrappedArray[Pos]) extends Serializable {

  @throws(classOf[IOException])
  private def writeObject(out: ObjectOutputStream): Unit = {
    out.writeUTF(surface)
    out.writeShort(leftId)
    out.writeShort(rightId)
    out.writeInt(cost)

    out.writeUTF(feature.mkString(","))
    out.writeUTF(poses.map(_.toString).mkString(","))
  }

  @throws(classOf[IOException])
  private def readObject(in: ObjectInputStream): Unit =  {
    surface = in.readUTF()
    leftId = in.readShort()
    rightId = in.readShort()
    cost = in.readInt()

    feature = wrapRefArray(in.readUTF().split(","))
    poses = wrapRefArray(in.readUTF().split(",").map(Pos.withName))

  }

}

