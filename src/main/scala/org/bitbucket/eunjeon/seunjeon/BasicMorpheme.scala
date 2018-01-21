package org.bitbucket.eunjeon.seunjeon

import java.io.{IOException, ObjectInputStream, ObjectOutputStream}

import com.typesafe.scalalogging.Logger
import org.bitbucket.eunjeon.seunjeon.MorphemeType.MorphemeType
import org.bitbucket.eunjeon.seunjeon.Pos.Pos
import org.slf4j.LoggerFactory

import scala.collection.mutable


object BasicMorpheme {
  val logger = Logger(LoggerFactory.getLogger(classOf[Morpheme].getName))

  def apply(surface: String,
            leftId: Short,
            rightId: Short,
            cost: Int,
            feature: String,
            mType: MorphemeType,
            poses: mutable.WrappedArray[Pos]) = {
    new BasicMorpheme().
      setSurface(surface).
      setLeftId(leftId).
      setRightId(rightId).
      setCost(cost).
      setFeature(feature).
      setMType(mType).
      setPoses(poses)
  }

  def apply(morpheme: Morpheme): BasicMorpheme = {
    new BasicMorpheme().
      setSurface(morpheme.getSurface).
      setLeftId(morpheme.getLeftId).
      setRightId(morpheme.getRightId).
      setCost(morpheme.getCost).
      setFeature(morpheme.getFeature).
      setMType(morpheme.getMType).
      setPoses(morpheme.getPoses)
  }

  def deComposite(feature7:String): Seq[BasicMorpheme] = {
    try {
      for (feature7 <- feature7.split("[+]")) yield BasicMorpheme.createFromFeature7(feature7)
    } catch {
      case _: Throwable =>
        logger.warn(s"invalid feature7 format : $feature7")
        Seq[BasicMorpheme]()
    }
  }

  /**
    *
    * @param feature7  "은전/NNG/\*"
    */
  def createFromFeature7(feature7: String): BasicMorpheme = {
    val splited = feature7.split("/")

    new BasicMorpheme().
      setSurface(splited(0)).
      setLeftId(-1).
      setRightId(-1).
      setCost(0).
      setFeature(splited(1)).
      setMType(MorphemeType.COMMON).
      setPoses(Array(Pos(splited(1))))

//    MorphemeBasic(
//      splited(0),
//      -1, -1, 0,
//      wrapRefArray(Array[String](splited(1))), // TODO: feature 를 적당히 만들어 주자.
//      MorphemeType.COMMON,
//      wrapRefArray(Array(Pos(splited(1)))))
  }
}

// TODO: sbt publish 할때 사전 빌드해도록 하고 SerialVersionUID 빼자
@SerialVersionUID(1000L)
class BasicMorpheme extends Morpheme with Serializable {
  private var surface: String = _
  private var leftId: Short = _
  private var rightId: Short = _
  private var cost: Int = _
  private var feature: String = _
  private var mType: MorphemeType = _
  private var poses: mutable.WrappedArray[Pos] = _

  private var composite: String = _
  private var featureHead: String = _

  def setSurface(s: String): BasicMorpheme = {
    surface = s
    this
  }

  def setLeftId(id: Short): BasicMorpheme = {
    leftId = id
    this
  }

  def setRightId(id: Short): BasicMorpheme = {
    rightId = id
    this
  }

  def setCost(c: Int): BasicMorpheme = {
    cost = c
    this
  }

  def setFeature(f: String): BasicMorpheme = {
    feature = f
    val splitedFeature = f.split(",")

    composite = if (splitedFeature.length >= 7) splitedFeature(7) else null
    featureHead = if (splitedFeature.nonEmpty) splitedFeature(0) else null
    this
  }

  def setMType(m: MorphemeType): BasicMorpheme = {
    mType = m
    this
  }

  def setPoses(ps: mutable.WrappedArray[Pos]): BasicMorpheme = {
    poses = ps
    this
  }

  override def getSurface: String = surface
  override def getLeftId: Short = leftId
  override def getRightId: Short = rightId
  override def getCost: Int = cost
  override def getFeature: String = feature
  override def getFeatureHead: String = featureHead
  override def getMType: MorphemeType = mType
  override def getPoses: mutable.WrappedArray[Pos] = poses

  override def deComposite(): Seq[Morpheme] = BasicMorpheme.deComposite(composite)

  @throws(classOf[IOException])
  private def writeObject(out: ObjectOutputStream): Unit = {
    out.writeUTF(surface)
    out.writeShort(leftId)
    out.writeShort(rightId)
    out.writeInt(cost)

    out.writeUTF(feature)
    out.writeInt(mType.id)
    out.writeUTF(poses.map(_.id).mkString(","))
  }

  @throws(classOf[IOException])
  private def readObject(in: ObjectInputStream): Unit =  {
    surface = in.readUTF()
    leftId = in.readShort()
    rightId = in.readShort()
    cost = in.readInt()

    feature = in.readUTF()
    mType = MorphemeType(in.readInt())
    poses = wrapRefArray(in.readUTF().split(",").map(id => Pos(id.toInt)))
  }

  override def equals(o: Any) = o match {
    case that: BasicMorpheme => getSurface == that.getSurface && leftId == that.leftId && rightId == that.rightId
    case _ => false
  }

  def key: String = s"$getSurface $leftId $rightId"

  override def hashCode = s"$getSurface $leftId $rightId".hashCode
}

