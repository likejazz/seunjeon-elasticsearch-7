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

import java.nio.file.{Paths, Files}
import java.nio.file.StandardCopyOption._


object DicBuilder {
  val RESOURCE_PATH = "src/main/resources"
  
  def main(args: Array[String]): Unit = {
    println("compiling lexicon dictionary...")
    buildLexiconDict(RESOURCE_PATH)

    println("compiling connection-cost dictionary...")
    buildConnectionCostDict(RESOURCE_PATH)

    copyCharDef(RESOURCE_PATH)
    copyUnkDef(RESOURCE_PATH)

    println("complete")

  }

  private def copyCharDef(resourcePath: String): Unit = {
    Files.copy(Paths.get("mecab-ko-dic/char.def"), Paths.get(RESOURCE_PATH + "/char.def"), REPLACE_EXISTING)
  }

  private def copyUnkDef(resourcePath: String): Unit = {
    Files.copy(Paths.get("mecab-ko-dic/unk.def"), Paths.get(RESOURCE_PATH + "/unk.def"), REPLACE_EXISTING)
  }

  private def buildConnectionCostDict(resourcePath: String): Unit = {
    val connectionCostDict = new ConnectionCostDict
    connectionCostDict.loadFromFile("mecab-ko-dic/matrix.def")
    connectionCostDict.save(
      resourcePath + ConnectionCostDict.resourceConnDicFile)
  }

  private def buildLexiconDict(resourcePath: String): Unit = {
    val lexiconDict = new LexiconDict
    lexiconDict.loadFromCsvFiles("mecab-ko-dic")
    lexiconDict.save(
      resourcePath + LexiconDict.lexiconResourceFile,
      resourcePath + LexiconDict.lexiconTrieResourceFile)
  }
}
