package org.bitbucket.eunjeon.seunjeon
import org.bitbucket.eunjeon.seunjeon.MorphemeType.MorphemeType
import org.bitbucket.eunjeon.seunjeon.Pos.Pos

import scala.collection.mutable

object UnkMorpheme {
  def apply(surface: String,
            leftId: Short,
            rightId: Short,
            cost: Int,
            feature: String,
            mType: MorphemeType,
            poses: mutable.WrappedArray[Pos]) = {
    new UnkMorpheme().
      setSurface(surface).
      setLeftId(leftId).
      setRightId(rightId).
      setCost(cost).
      setFeature(feature).
      setMType(mType).
      setPoses(poses)
  }

  def apply(morpheme: Morpheme): UnkMorpheme = {
    new UnkMorpheme().
      setSurface(morpheme.getSurface).
      setLeftId(morpheme.getLeftId).
      setRightId(morpheme.getRightId).
      setCost(morpheme.getCost).
      setFeature(morpheme.getFeature).
      setMType(morpheme.getMType).
      setPoses(morpheme.getPoses)
  }

  def apply(surface:String, morpheme: Morpheme): UnkMorpheme = {
    UnkMorpheme.apply(morpheme).setSurface(surface)
  }

}

class UnkMorpheme extends Morpheme {
  private var surface: String = _
  private var leftId: Short = _
  private var rightId: Short = _
  private var cost: Int = _
  private var feature: String = _
  private var mType: MorphemeType = _
  private var poses: mutable.WrappedArray[Pos] = _

  def setSurface(s: String): UnkMorpheme = {
    surface = s
    this
  }

  def setLeftId(id: Short): UnkMorpheme = {
    leftId = id
    this
  }

  def setRightId(id: Short): UnkMorpheme = {
    rightId = id
    this
  }

  def setCost(c: Int): UnkMorpheme = {
    cost = c
    this
  }

  def setFeature(f: String): UnkMorpheme = {
    feature = f
    this
  }

  def setMType(m: MorphemeType): UnkMorpheme = {
    mType = m
    this
  }

  def setPoses(ps: mutable.WrappedArray[Pos]): UnkMorpheme = {
    poses = ps
    this
  }
  override def getSurface: String = surface
  override def getLeftId: Short = leftId
  override def getRightId: Short = rightId
  override def getCost: Int = cost
  override def getFeature: String = feature
  override def getFeatureHead: String = feature.substring(0,2)
  override def getMType: MorphemeType = mType
  override def getPoses: mutable.WrappedArray[Pos] = poses
  override def deComposite(): Seq[Morpheme] = Seq.empty
}
