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

  def compress(morphemes: Seq[Morpheme]): Array[CompressedMorpheme] = {
    val compressedMorphemes = morphemes.par.map(new CompressedMorpheme(_))
    return compressedMorphemes.toArray;
  }
}

/**
  * Compressed version of Morpheme class that saves memory by
  * compressing the feature array, surface, mType and poses fields
  *
  * @param morpheme
  */
@SerialVersionUID(1001L)
class CompressedMorpheme(morpheme: Morpheme) extends Serializable {
  val logger = Logger(LoggerFactory.getLogger(classOf[CompressedMorpheme].getName))

  var leftId: Short = morpheme.leftId
  var rightId: Short = morpheme.rightId
  var cost: Int = morpheme.cost

  var _mType: Byte = morpheme.mType.id.toByte

  def mType = MorphemeType(Byte.byte2int(_mType))

  //storing poses as bytes instead of array of enums
  private var _poses: Array[Byte] = {
    val buffer = ByteBuffer.allocate(morpheme.poses.length)
    buffer.rewind()
    for (pose <- morpheme.poses) {
      buffer.put(pose.id.byteValue())
    }
    buffer.array()
  }

  def poses = {
    val posesArr: util.ArrayList[Pos] = new util.ArrayList[Pos];
    for (pose <- _poses) {
      posesArr.add(Pos(Byte.byte2int(pose)))
    }
    wrapRefArray(posesArr.toArray).asInstanceOf[mutable.WrappedArray[Pos]]
  }

  private var _surface: Array[Byte] = CompressionHelper.compressStr(morpheme.surface)

  def surface = CompressionHelper.uncompressStr(_surface)

  def surface_=(value: String) = _surface = CompressionHelper.compressStr(value)

  private var _feature: mutable.WrappedArray[String] = CompressedMorpheme.deDupeFeatureArray(morpheme.feature)

  def feature: mutable.WrappedArray[String] =_feature

  def feature_=(value: mutable.WrappedArray[String]) = _feature = CompressedMorpheme.deDupeFeatureArray(value)

  def uncompressed = new Morpheme(surface, leftId, rightId, cost, feature, mType, poses)

  @throws(classOf[IOException])
  private def writeObject(out: ObjectOutputStream): Unit = {
    out.writeObject(_surface)
    out.writeShort(leftId)
    out.writeShort(rightId)
    out.writeInt(cost)

    out.writeObject(_feature)
    out.writeByte(_mType)
    out.writeObject(_poses)
  }

  @throws(classOf[IOException])
  private def readObject(in: ObjectInputStream): Unit = {
    _surface = in.readObject().asInstanceOf[Array[Byte]]
    leftId = in.readShort()
    rightId = in.readShort()
    cost = in.readInt()

    _feature = in.readObject().asInstanceOf[mutable.WrappedArray[String]]
    _mType = in.readByte()
    _poses = in.readObject().asInstanceOf[Array[Byte]]
  }

}
