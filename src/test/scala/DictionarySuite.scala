import org.scalatest.FunSuite

/**
 * Created by parallels on 7/27/15.
 */
class DictionarySuite extends FunSuite {

  test("load dictionary") {
    val dictionary = new Dictionary()
    dictionary.loadDict()
    val result = dictionary.parseText("감자고구마오징어호박")
    println(result)
  }

}
