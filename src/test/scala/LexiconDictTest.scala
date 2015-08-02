import org.scalatest.FunSuite


class LexiconDictTest extends FunSuite {
  test("save and open") {
    val lexicons =
      """감자,1,2,100,NNG,*,F,감자,*,*,*,*,*
        |고구마,1,2,100,NNG,*,F,고구마,*,*,*,*,*
        |고,1,2,100,NNG,*,F,고,*,*,*,*,*
        |구마,1,2,100,NNG,*,F,구마,*,*,*,*,*
        |오징어,1,2,100,NNG,*,F,오징어,*,*,*,*,*"""
    val saveLexiconDict = new LexiconDict
    saveLexiconDict.loadFromString(lexicons)
    saveLexiconDict.save()

    val openLexiconDict = new LexiconDict
    openLexiconDict.open()
    val result = openLexiconDict.prefixSearch("고구마")
    result.foreach(t => println(t.surface))
  }

  test("performance") {
    {
      val startTime = System.nanoTime()
      val lexiconDict = new LexiconDict
      lexiconDict.loadFromPath("/home/parallels/Downloads/mecab-ko-dic-1.6.1-20140814")
      val endTime = System.nanoTime()
      val elapsedTime = (endTime - startTime)
      println(s"$elapsedTime ns")
      lexiconDict.save()
    }
    {
      val startTime = System.nanoTime()
      val lexiconDict = new LexiconDict
      lexiconDict.open()
      val endTime = System.nanoTime()
      val elapsedTime = (endTime - startTime)
      println(s"$elapsedTime ns")
    }

  }

}
