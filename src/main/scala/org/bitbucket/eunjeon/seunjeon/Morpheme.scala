package org.bitbucket.eunjeon.seunjeon

import org.bitbucket.eunjeon.seunjeon.MorphemeType.MorphemeType
import org.bitbucket.eunjeon.seunjeon.Pos.Pos

import scala.collection.mutable

trait Morpheme {
  def getSurface: String
  def getLeftId: Short
  def getRightId: Short
  def getCost: Int

  def getFeature: mutable.WrappedArray[String]
  def getMType: MorphemeType
  def getPoses:mutable.WrappedArray[Pos]

  def deComposite(): Seq[Morpheme]

  override def toString: String = s"$getSurface/$getPoses"
}
