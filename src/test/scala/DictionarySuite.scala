import org.scalatest._

class DictionarySuite extends FunSuite with BeforeAndAfter {
  var dictionary:Tagger = null
  var lexicons: String = null
  var connectionCosts: String = null

  before {
    lexicons =
      """감자,1,2,100,NNG,*,F,감자,*,*,*,*,*
        |고구마,1,2,100,NNG,*,F,고구마,*,*,*,*,*
        |고,1,2,100,NNG,*,F,고구마,*,*,*,*,*
        |구마,1,2,100,NNG,*,F,고구마,*,*,*,*,*
        |오징어,1,2,100,NNG,*,F,오징어,*,*,*,*,*"""

    connectionCosts =
      """3 2
        |0 1 10
        |2 1 20
        |2 0 30"""
    dictionary = new Tagger()
    // TODO: apply factory function
    val lexiconDict = new LexiconDict
    lexiconDict.loadFromString(lexicons)
    dictionary.setLexiconDict(lexiconDict)
    val connectionCostDict = new ConnectionCostDict
    connectionCostDict.loadFromString(connectionCosts)
    dictionary.setConnectionCostDict(connectionCostDict)
  }

  test("testParseOneEojeol") {
    assert("BOS,감자,고구마,오징어,EOS" ==
      dictionary.parseText("감자고구마오징어").map(_.surface).mkString(","))
  }

  test("testParseMultipleEojeol") {
    assert("BOS,감자,고구마,오징어,EOS" ==
      dictionary.parseText("감자고구마 오징어").map(_.surface).mkString(","))

    assert("BOS,감자,고,구마,오징어,EOS" ==
      dictionary.parseText("감자고 구마 오징어").map(_.surface).mkString(","))
  }

  test("unknown word") {
    assert("BOS,감자,호박,오징어,EOS" ==
      dictionary.parseText("감자호박오징어").map(_.surface).mkString(","))
  }

  test("long eojeol") {
    assert("BOS,감자,호박,오징어,감자,호박,오징어,감자,호박,오징어,감자,호박,오징어,감자,호박,오징어,감자,호박,오징어,EOS" ==
      dictionary.parseText("감자호박오징어감자호박오징어감자호박오징어감자호박오징어감자호박오징어감자호박오징어").map(_.surface).mkString(","))
  }
}
