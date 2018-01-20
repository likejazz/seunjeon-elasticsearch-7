package org.bitbucket.eunjeon.seunjeon

import org.scalatest._

class TokenizerTest extends FunSuite with BeforeAndAfter {
  var tokenizer:Tokenizer = null
  var lexicons: String = null
  var connectionCosts: String = null

  before {
    lexicons =
      """감자,1,2,100,NNG,*,F,감자,*,*,*,*,*
        |고구마,1,2,100,NNG,*,F,고구마,*,*,*,*,*
        |고,1,2,100,NNG,*,F,고,*,*,*,*,*
        |구마,1,2,100,NNG,*,F,구마,*,*,*,*,*
        |오징어,1,2,100,NNG,*,F,오징어,*,*,*,*,*""".stripMargin

    // HANGUL,1803,3564,9396,UNKNOWN,*,*,*,*,*,*,*
    connectionCosts =
      """3565 1804
        |0 1 10
        |2 1 100
        |2 0 30
        |0 1803 100
        |2 1803 100
        |3564 1 100
        |3564 0 100""".stripMargin
    // TODO: apply factory function
    val lexiconDict = new LexiconDict
    lexiconDict.loadFromString(lexicons, compress = false)

    val connectionCostDict = new ConnectionCostDict
    connectionCostDict.loadFromString(connectionCosts)

    tokenizer = new Tokenizer(lexiconDict, connectionCostDict, false)
  }

  test("testParseOneEojeol") {
    assert("감자,고구마,오징어" ==
      tokenizer.parseText("감자고구마오징어", dePreAnalysis = true).flatMap(_.nodes).map(_.morpheme.getSurface).mkString(","))
  }

  test("testParseMultipleEojeol") {
    assert("감자,고구마,오징어" ==
      tokenizer.parseText("감자고구마 오징어", dePreAnalysis = true).flatMap(_.nodes).map(_.morpheme.getSurface).mkString(","))

    assert("감자,고,구마,오징어" ==
      tokenizer.parseText("감자고 구마 오징어", dePreAnalysis = true).flatMap(_.nodes).map(_.morpheme.getSurface).mkString(","))
  }

  /* TODO: 나중에 정리하자.
  test("unknown word") {
    assert("감자,호박,오징어" ==
      tokenizer.parseText("감자호박오징어", true).map(_.morpheme.surface).mkString(","))
  }

  test("long eojeol") {
    assert("감자,호박,오징어,감자,호박,오징어,감자,호박,오징어,감자,호박,오징어,감자,호박,오징어,감자,호박,오징어" ==
      tokenizer.parseText("감자호박오징어감자호박오징어감자호박오징어감자호박오징어감자호박오징어감자호박오징어", true).map(_.morpheme.surface).mkString(","))
  }
  */

}
