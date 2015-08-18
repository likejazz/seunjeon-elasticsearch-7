/**
 * Copyright 2015 youngho yu, yongwoon lee
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package org.bitbucket.eunjeon.seunjeon


object DicBuilder {
  def main(args: Array[String]): Unit = {
    val resourcePath = "src/main/resources"
    println("compiling lexicon dictionary...")
    val lexiconDict = new LexiconDict
    lexiconDict.loadFromCsvFiles("mecab-ko-dic")
    lexiconDict.save(
      resourcePath  + LexiconDict.lexiconResourceFile,
      resourcePath + LexiconDict.lexiconTrieResourceFile)

    println("compiling connection-cost dictionary...")
    val connectionCostDict = new ConnectionCostDict
    connectionCostDict.loadFromFile("mecab-ko-dic/matrix.def")
    connectionCostDict.save(
      resourcePath + ConnectionCostDict.resourceConnDicFile)

    println("complete")

  }

}
