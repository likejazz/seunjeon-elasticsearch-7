package org.bitbucket.eunjeon.seunjeon.elasticsearch

import org.bitbucket.eunjeon.seunjeon.Pos.Pos
import org.bitbucket.eunjeon.seunjeon._
import org.scalatest.FunSuite


class TokenizerHelperTest extends FunSuite {

  test("toLuceneTokens-oneNode") {
    val input = Seq(
      Eojeol("aa", 0, 2, Seq(LNode(DummyMorpheme("aa", Pos.N), 0, 2))),
      Eojeol("cc", 3, 5, Seq(LNode(DummyMorpheme("cc", Pos.N), 3, 5)))
    )

    assert(
      TokenizerHelper.toLuceneTokens(input, indexEojeol = true, posTagging = false) ===
        Seq(LuceneToken("aa", 1, 1, 0, 2, "N"), LuceneToken("cc", 1, 1, 3, 5, "N"))
    )
  }

  test("toLuceneTokens") {
    val input = Seq(
      Eojeol("aabb", 0, 4,
        Seq(LNode(DummyMorpheme("aa", Pos.N), 0, 2), LNode(DummyMorpheme("bb", Pos.N), 2, 4))),
      Eojeol("ccdd", 5, 9,
        Seq(LNode(DummyMorpheme("cc", Pos.N), 5, 7), LNode(DummyMorpheme("dd", Pos.N), 7, 9)))
    )

    assert(
      TokenizerHelper.toLuceneTokens(input, indexEojeol = true, posTagging = false) ===
        Seq(
          LuceneToken("aa",1,1,0,2,"N"),
          LuceneToken("aabb",0,2,0,4,"EOJ"),
          LuceneToken("bb",1,1,2,4,"N"),

          LuceneToken("cc",1,1,5,7,"N"),
          LuceneToken("ccdd",0,2,5,9,"EOJ"),
          LuceneToken("dd",1,1,7,9,"N")
        )
    )
  }

  test("flattenNodes2") {
    val input = Seq(
      Eojeol("aabb", 0, 4,
        Seq(LNode(DummyMorpheme("bb", Pos.N), 2, 4))),
      Eojeol("ccdd", 5, 9,
        Seq(LNode(DummyMorpheme("cc", Pos.N), 5, 7), LNode(DummyMorpheme("dd", Pos.N), 7, 9)))
    )

    assert(
      TokenizerHelper.toLuceneTokens(input, indexEojeol = true, posTagging = false) ===
        Seq(
          LuceneToken("aabb",1,1,0,4,"EOJ"),
          LuceneToken("bb",0,1,2,4,"N"),

          LuceneToken("cc",1,1,5,7,"N"),
          LuceneToken("ccdd",0,2,5,9,"EOJ"),
          LuceneToken("dd",1,1,7,9,"N")
        )
    )
  }
  /*
  test("_toLuceneTokens-eojeolFalse,posFalse") {
    val input: Seq[OffsetNode] = Seq(
      LNode(DummyMorpheme("a", Pos.N), 0, 1),
      LNode(DummyMorpheme("b", Pos.N), 2, 3)
    )

    assert(
      TokenizerHelper._toLuceneTokens(input, indexEojeol = false, posTagging = false) ===
        Seq(LuceneToken("a", 1, 1, 0, 1, "N"), LuceneToken("b", 1, 1, 2, 3, "N"))
    )
  }

  test("_toLuceneTokens-eojeolTrue,posFalse") {
    val input: Seq[OffsetNode] =
      Seq(
        LNode(DummyMorpheme("aa", Pos.N), 0, 2),
        Eojeol("aabb", 0, 4, Seq(LNode(DummyMorpheme("aa", Pos.N), 0, 2), LNode(DummyMorpheme("bb", Pos.N), 2, 4))),
        LNode(DummyMorpheme("bb", Pos.N), 2, 4),
        LNode(DummyMorpheme("cc", Pos.N), 5, 7),
        Eojeol("ccdd", 5, 9, Seq(LNode(DummyMorpheme("cc", Pos.N), 5, 7), LNode(DummyMorpheme("dd", Pos.N), 7, 9))),
        LNode(DummyMorpheme("dd", Pos.N), 7, 9)
      )

    assert(
      TokenizerHelper._toLuceneTokens(input, indexEojeol = true, posTagging = false) ===
        Seq(
          LuceneToken("aa",1,1,0,2,"N"),
          LuceneToken("aabb",0,2,0,4,"EOJ"),
          LuceneToken("bb",1,1,2,4,"N"),

          LuceneToken("cc",1,1,5,7,"N"),
          LuceneToken("ccdd",0,2,5,9,"EOJ"),
          LuceneToken("dd",1,1,7,9,"N")
        )
    )
  }

  test("_toLuceneTokens-eojeolTrue,posFalse2") {
    val input: Seq[OffsetNode] =
      Seq(
        Eojeol("aabb", 0, 4, Seq(LNode(DummyMorpheme("aa", Pos.N), 0, 2), LNode(DummyMorpheme("bb", Pos.N), 2, 4))),
        LNode(DummyMorpheme("bb", Pos.N), 2, 4),
        LNode(DummyMorpheme("cc", Pos.N), 5, 7),
        Eojeol("ccdd", 5, 9, Seq(LNode(DummyMorpheme("cc", Pos.N), 5, 7), LNode(DummyMorpheme("dd", Pos.N), 7, 9))),
        LNode(DummyMorpheme("dd", Pos.N), 7, 9)
      )

    assert(
      TokenizerHelper._toLuceneTokens(input, indexEojeol = true, posTagging = false) ===
        Seq(
          LuceneToken("aabb",1,2,0,4,"EOJ"),
          LuceneToken("bb",0,1,2,4,"N"),

          LuceneToken("cc",1,1,5,7,"N"),
          LuceneToken("ccdd",0,2,5,9,"EOJ"),
          LuceneToken("dd",1,1,7,9,"N")
        )
    )
  }
  */

  def DummyMorpheme(surface: String, pos: Pos): Morpheme =
    BasicMorpheme(surface, 0.toShort, 0.toShort, 0, "", MorphemeType.COMMON, Array(pos))

}
