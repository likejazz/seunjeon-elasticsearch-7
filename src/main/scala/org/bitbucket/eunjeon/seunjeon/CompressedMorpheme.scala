package org.bitbucket.eunjeon.seunjeon

import java.io.{IOException, ObjectInputStream, ObjectOutputStream}

import com.typesafe.scalalogging.Logger
import org.bitbucket.eunjeon.seunjeon.Pos.Pos
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import java.nio.ByteBuffer
import java.util

object CompressedMorpheme {
  def deDupeFeatureArray(feature: mutable.WrappedArray[String]): mutable.WrappedArray[String] = {
    val result_features = ArrayBuffer[String]();
    for (i <- 0 until feature.length) {
      val feature_value = feature(i)
      result_features.append(CompressionHelper.getStrCached(feature_value));
    }
    return wrapRefArray(result_features.toArray)
  }

  def unCompressFeatureArray(compressedArray: Array[Array[Byte]]): Array[String] = {
    val uncompressStringArray = ArrayBuffer[String]();
    for (i <- 0 until compressedArray.length) {
      val compressedValue = compressedArray(i)
      uncompressStringArray.append(CompressionHelper.uncompressStr(compressedValue));
    }
    return uncompressStringArray.toArray
  }

  def compress(morphemes: Seq[BasicMorpheme]): Array[CompressedMorpheme] =
    morphemes.par.map(new CompressedMorpheme(_)).toArray
}

/**
  * Compressed version of Morpheme class that saves memory by
  * compressing the feature array, surface, mType and poses fields
  *
  * @param morpheme
  */
class CompressedMorpheme(morpheme: Morpheme) extends Morpheme with Serializable {
  private val logger = Logger(LoggerFactory.getLogger(classOf[CompressedMorpheme].getName))

  private var surface: Array[Byte] = CompressionHelper.compressStr(morpheme.getSurface)
  private var leftId: Short = morpheme.getLeftId
  private var rightId: Short = morpheme.getRightId
  private var cost: Int = morpheme.getCost
  private var feature: mutable.WrappedArray[String] = CompressedMorpheme.deDupeFeatureArray(morpheme.getFeature)

  private var mType: Byte = morpheme.getMType.id.toByte

  //storing poses as bytes instead of array of enums
  private var poses: Array[Byte] = {
    val buffer = ByteBuffer.allocate(morpheme.getPoses.length)
    buffer.rewind()
    for (pose <- morpheme.getPoses) {
      buffer.put(pose.id.byteValue())
    }
    buffer.array()
  }

  override def getSurface: String = CompressionHelper.uncompressStr(surface)
  override def deComposite(): Seq[Morpheme] = BasicMorpheme.deComposite(feature(7))
  override def getLeftId: Short = leftId
  override def getRightId: Short = rightId
  override def getCost: Int = cost
  override def getFeature: mutable.WrappedArray[String] = feature
  override def getMType = MorphemeType(Byte.byte2int(mType))
  override def getPoses: mutable.WrappedArray[Pos] = {
    val posesArr: util.ArrayList[Pos] = new util.ArrayList[Pos]
    for (pose <- poses) {
      posesArr.add(Pos(Byte.byte2int(pose)))
    }
    wrapRefArray(posesArr.toArray).asInstanceOf[mutable.WrappedArray[Pos]]
  }

//  def feature_=(value: mutable.WrappedArray[String]) = _feature = CompressedMorpheme.deDupeFeatureArray(value)

  def uncompressed = BasicMorpheme(this)

  @throws(classOf[IOException])
  private def writeObject(out: ObjectOutputStream): Unit = {
    out.writeObject(surface)
    out.writeShort(leftId)
    out.writeShort(rightId)
    out.writeInt(cost)
    out.writeObject(feature)
    out.writeByte(mType)
    out.writeObject(poses)
  }

  @throws(classOf[IOException])
  private def readObject(in: ObjectInputStream): Unit = {
    surface = in.readObject().asInstanceOf[Array[Byte]]
    leftId = in.readShort()
    rightId = in.readShort()
    cost = in.readInt()

    feature = in.readObject().asInstanceOf[mutable.WrappedArray[String]]
    mType = in.readByte()
    poses = in.readObject().asInstanceOf[Array[Byte]]
  }

}
