import org.scalatest.FunSuite

/**
 * Created by parallels on 8/1/15.
 */
class PerformanceSuite extends FunSuite {

  test("performance") {
    val dictionary = new Dictionary()
    dictionary.loadDict()
    val times = 10000
    val startTime = System.nanoTime()
    for (i <- 0 until times) {
      dictionary.parseText("감자호박오징어").map(_.surface).mkString(",")
    }
    val endTime = System.nanoTime()
    val elapsedTime = (endTime - startTime) / times
    println(elapsedTime)
    println(s"$elapsedTime ns")
  }

}
