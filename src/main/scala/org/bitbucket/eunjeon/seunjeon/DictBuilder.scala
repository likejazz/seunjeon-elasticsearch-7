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

import java.nio.file.StandardCopyOption._
import java.nio.file.{Files, Paths}

import scala.reflect.io.{File, Path}


object DictBuilder {

  val workingDirectory = System.getProperty("user.dir")
  val MECAB_KO_DIC_PATH = workingDirectory + "/mecab-ko-dic"
  val RESOURCE_PATH = workingDirectory + "/src/main/resources"
  val DICT_PATHNAME = "/dictionary"
  val DICT_PATH = DICT_PATHNAME

  val TERM_DICT_FILENAME = "/termDict.dat"
  val TERM_DICT = DICT_PATH + TERM_DICT_FILENAME
  val DICT_MAPPER_FILENAME = "/dictMapper.dat"
  val DICT_MAPPER = DICT_PATH + DICT_MAPPER_FILENAME
  val TERM_TRIE_FILENAME = "/trie.dat"
  val TERM_TRIE = DICT_PATH + TERM_TRIE_FILENAME
  val CONNECTION_COST_FILENAME = "/connection_cost.dat"
  val CONNECTION_COST = DICT_PATH + CONNECTION_COST_FILENAME

  val UNK_DEF_FILENAME = "/unk.def"
  val UNK_DEF = DICT_PATH + UNK_DEF_FILENAME

  val CHAR_DEF_FILENAME = "/char.def"

  val LEFT_ID_DEF_FILENAME = "/left-id.def"
  val LEFT_ID_DEF = DICT_PATH + LEFT_ID_DEF_FILENAME
  val RIGHT_ID_DEF_FILENAME = "/right-id.def"
  val RIGHT_ID_DEF = DICT_PATH + RIGHT_ID_DEF_FILENAME

  def main(args: Array[String]): Unit = {
    clear()

    copyUnkDef()
    copyLeftIdDef()
    copyRightIdDef()

    println("compiling lexicon dictionary...")
    buildLexiconDict()

    println("compiling connection-cost dictionary...")
    buildConnectionCostDict()

    println("complete")
  }

  private def copyUnkDef(): Unit = {
    copyDefFile(UNK_DEF_FILENAME)
  }

  private def copyLeftIdDef(): Unit = {
    copyDefFile(LEFT_ID_DEF_FILENAME)
  }

  private def copyRightIdDef(): Unit = {
    copyDefFile(RIGHT_ID_DEF_FILENAME)
  }

  private def copyDefFile(defFileName: String): Unit = {
    Files.copy(Paths.get(MECAB_KO_DIC_PATH + File.separator + defFileName),
      Paths.get(RESOURCE_PATH + File.separator +
        DICT_PATHNAME + File.separator + defFileName), REPLACE_EXISTING)
  }

  private def buildConnectionCostDict(): Unit = {
    val connectionCostDict = new ConnectionCostDict
    connectionCostDict.loadFromFile(MECAB_KO_DIC_PATH + File.separator + "matrix.def")
    connectionCostDict.save(RESOURCE_PATH  + File.separator +
      DICT_PATHNAME + File.separator + CONNECTION_COST_FILENAME)
    println("building connection cost dictionary OK. " +
      s"(${connectionCostDict.getDictionaryInfo()})")
  }

  private def buildLexiconDict(): Unit = {
    val dictPath = RESOURCE_PATH + File.separator + DICT_PATHNAME

    val lexiconDict = new LexiconDict
    lexiconDict.loadFromDir(MECAB_KO_DIC_PATH)
    lexiconDict.save(dictPath + File.separator + TERM_DICT_FILENAME,
                     dictPath + File.separator + DICT_MAPPER_FILENAME,
                     dictPath + File.separator + TERM_TRIE_FILENAME)
    println("building LexiconDict OK. " +
      s"(${lexiconDict.getDictionaryInfo})")
  }

  private def clear(): Unit = {
    val dictPath = RESOURCE_PATH + File.separator + DICT_PATHNAME

    Path(dictPath).deleteRecursively()
    new java.io.File(dictPath).mkdirs()
  }
}
