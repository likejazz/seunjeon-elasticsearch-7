import org.scalatest.{BeforeAndAfter, FunSuite}


class PerformanceSuite extends FunSuite with BeforeAndAfter {
  var dictionary: Tagger = null

  before {
    dictionary = new Tagger()

    val lexiconDict = new LexiconDict
    lexiconDict.loadFromPath("/home/parallels/Downloads/mecab-ko-dic-1.6.1-20140814")
    dictionary.setLexiconDict(lexiconDict)

    val connectionCostDict = new ConnectionCostDict
    connectionCostDict.loadFromFile("/home/parallels/Downloads/mecab-ko-dic-1.6.1-20140814/matrix.def")
    dictionary.setConnectionCostDict(connectionCostDict)
  }

  test("performance") {
    val times = 10000
    val startTime = System.nanoTime()
    for (i <- 0 until times) {
      dictionary.parseText("안녕하세요. 형태소분석기입니다. 서울에서 살고있습니다.")
    }
    val endTime = System.nanoTime()
    val elapsedTime = (endTime - startTime) / times
    println(elapsedTime)
    println(s"$elapsedTime ns")
  }

  test("sentence") {
    val result = dictionary.parseText("안녕하세요. 형태소분석기입니다.").map(t => t.surface + ":" + t.feature).mkString(",")
    println(result)
  }

}
