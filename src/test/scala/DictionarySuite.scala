import org.scalatest._

class DictionarySuite extends FunSuite with BeforeAndAfter {
  var dictionary:Dictionary = null
  var lexiconDictIter:Iterator[String] = null
  var costDictIter:Iterator[String] = null

  before {
    lexiconDictIter =
      """감자,1,2,100,NNG,*,F,감자,*,*,*,*,*
        |고구마,1,2,100,NNG,*,F,고구마,*,*,*,*,*
        |고,1,2,100,NNG,*,F,고구마,*,*,*,*,*
        |구마,1,2,100,NNG,*,F,고구마,*,*,*,*,*
        |오징어,1,2,100,NNG,*,F,오징어,*,*,*,*,*""".stripMargin.split("\n").toIterator

    costDictIter =
      """3 2
        |0 1 10
        |2 1 20
        |2 0 30""".stripMargin.split("\n").toIterator
    dictionary = new Dictionary()
    dictionary.loadDict(lexiconDictIter, costDictIter)
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
