import org.scalatest.FunSuite


class DictionarySuite extends FunSuite {
  val dictionary = new Dictionary()
  val lexiconDictIter =
    """감자,1,2,100,NNG,*,F,감자,*,*,*,*,*
      |고구마,1,2,100,NNG,*,F,고구마,*,*,*,*,*
      |고,1,2,100,NNG,*,F,고구마,*,*,*,*,*
      |구마,1,2,100,NNG,*,F,고구마,*,*,*,*,*
      |오징어,1,2,100,NNG,*,F,오징어,*,*,*,*,*""".stripMargin.split("\n").toIterator

  val costDictIter =
    """3 2
      |0 1 10
      |2 1 20
      |2 0 30""".stripMargin.split("\n").toIterator


  test("parse one eojeol") {
    dictionary.loadDict(lexiconDictIter, costDictIter)
    assert("BOS,감자,고구마,오징어,EOS" ==
      dictionary.parseText("감자고구마오징어").map(_.surface).mkString(","))
  }

  test("parse multiple eojeol") {
    assert("BOS,감자,고구마,오징어,EOS" ==
      dictionary.parseText("감자고구마 오징어").map(_.surface).mkString(","))
  }


}
