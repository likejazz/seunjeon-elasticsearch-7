package org.bitbucket.eunjeon.seunjeon

import java.io._

import org.scalatest.{BeforeAndAfter, FunSuite}

/**
  * Created by parallels on 12/2/15.
  */
class SerializeTermDictTest  extends FunSuite with BeforeAndAfter {
  test("serialize TermDict") {
    val m1 = BasicMorpheme("hello", 1, 2, 100, Array("!", "@").mkString(","), MorphemeType.COMMON, Array(Pos.N))
    val m2 = BasicMorpheme("hello", 1, 2, 100, Array("!", "@").mkString(","), MorphemeType.COMMON, Array(Pos.N))

    val serial = Array(m1, m2)


    val output = new ObjectOutputStream(
      new BufferedOutputStream(new FileOutputStream("testDict.dat"), 16*1024))
    output.writeInt(serial.length)
    serial.foreach(output.writeObject(_))
    output.close()

    val input = new ObjectInputStream(
      new BufferedInputStream(new FileInputStream("testDict.dat"), 16*1024))
    val size = input.readInt()
    val restored = for (idx <- 0 until size) yield {
      input.readObject().asInstanceOf[BasicMorpheme]
    }
    input.close()
    restored.foreach(println)

  }

}
