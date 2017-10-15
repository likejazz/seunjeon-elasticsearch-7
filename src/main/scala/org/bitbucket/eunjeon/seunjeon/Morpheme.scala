package org.bitbucket.eunjeon.seunjeon

import java.io.{IOException, ObjectInputStream, ObjectOutputStream}

import com.typesafe.scalalogging.Logger
import org.bitbucket.eunjeon.seunjeon.MorphemeType.MorphemeType
import org.bitbucket.eunjeon.seunjeon.Pos.Pos
import org.slf4j.LoggerFactory

import scala.collection.mutable


object Morpheme {
  val logger = Logger(LoggerFactory.getLogger(classOf[Morpheme].getName))

  def apply(surface:String, morpheme: Morpheme): Morpheme = {
    Morpheme(surface,
      morpheme.leftId,
      morpheme.rightId,
      morpheme.cost, //morpheme.cost*surface.length,
      morpheme.feature,
      morpheme.mType,
      morpheme.poses)
  }

  def deComposite(feature7:String): Seq[Morpheme] = {
    try {
      for (feature7 <- feature7.split("[+]")) yield Morpheme.createFromFeature7(feature7)
    } catch {
      case _: Throwable =>
        logger.warn(s"invalid feature7 format : $feature7")
        Seq[Morpheme]()
    }
  }

  /**
    *
    * @param feature7  "은전/NNG/\*"
    */
  def createFromFeature7(feature7:String): Morpheme = {
    val splited = feature7.split("/")
    Morpheme(
      splited(0),
      -1, -1, 0,
      wrapRefArray(Array[String](splited(1))), // TODO: feature 를 적당히 만들어 주자.
      MorphemeType.COMMON,
      wrapRefArray(Array(Pos(splited(1)))))
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
// TODO: sbt publish 할때 사전 빌드해도록 하고 SerialVersionUID 빼자
@SerialVersionUID(1000L)
case class Morpheme(var surface:String,
                    var leftId:Short,
                    var rightId:Short,
                    var cost:Int,
                    var feature:mutable.WrappedArray[String],
                    var mType:MorphemeType,
                    var poses:mutable.WrappedArray[Pos]) extends Serializable {

  def deComposite(): Seq[Morpheme] = {
    Morpheme.deComposite(feature(7))
  }

  @throws(classOf[IOException])
  private def writeObject(out: ObjectOutputStream): Unit = {
    out.writeUTF(surface)
    out.writeShort(leftId)
    out.writeShort(rightId)
    out.writeInt(cost)

    out.writeUTF(feature.mkString(","))
    out.writeInt(mType.id)
    out.writeUTF(poses.map(_.id).mkString(","))
  }

  @throws(classOf[IOException])
  private def readObject(in: ObjectInputStream): Unit =  {
    surface = in.readUTF()
    leftId = in.readShort()
    rightId = in.readShort()
    cost = in.readInt()

    feature = wrapRefArray(in.readUTF().split(","))
    mType = MorphemeType(in.readInt())
    poses = wrapRefArray(in.readUTF().split(",").map(id => Pos(id.toInt)))
  }

  override def equals(o: Any) = o match {
    case that: Morpheme => surface == that.surface && leftId == that.leftId && rightId == that.rightId
    case _ => false
  }

  def key: String = s"$surface $leftId $rightId"

  override def hashCode = s"$surface $leftId $rightId".hashCode
}

