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

import scala.reflect.io.{Path, File}


object DictBuilder {

  val workingDirectory = System.getProperty("user.dir")
  val MECAB_KO_DIC_PATH = workingDirectory + File.separator + "mecab-ko-dic"
  val RESOURCE_PATH = workingDirectory + File.separator + "src/main/resources"
  val DICT_PATH = "dictionary"

  val TERM_DICT = "termDict.dat"
  val DICT_MAPPER = "dictMapper.dat"
  val TERM_TRIE = "trie.dat"

  val CHAR_DEF_FILE_NAME = "char.def"
  val UNK_DEF_FILE_NAME = "unk.def"
  val LEFT_ID_DEF_FILE_NAME = "left-id.def"
  val RIGHT_ID_DEF_FILE_NAME = "right-id.def"

  def main(args: Array[String]): Unit = {

    // TODO: reset resource/dict directory.
    println("compiling lexicon dictionary...")
    clear()
    buildLexiconDict()

    println("compiling connection-cost dictionary...")
    buildConnectionCostDict(RESOURCE_PATH + File.separator + DICT_PATH)

    copyCharDef(RESOURCE_PATH + File.separator + DICT_PATH)
    copyUnkDef(RESOURCE_PATH + File.separator + DICT_PATH)
    copyLeftIdDef(RESOURCE_PATH + File.separator + DICT_PATH)
    copyRightIdDef(RESOURCE_PATH + File.separator + DICT_PATH)

    println("complete")
  }

  private def copyCharDef(resourcePath: String): Unit = {
    copyDefFile(CHAR_DEF_FILE_NAME)
  }

  private def copyUnkDef(resourcePath: String): Unit = {
    copyDefFile(UNK_DEF_FILE_NAME)
  }

  private def copyLeftIdDef(resourcePath: String): Unit = {
    copyDefFile(LEFT_ID_DEF_FILE_NAME)
  }

  private def copyRightIdDef(resourcePath: String): Unit = {
    copyDefFile(RIGHT_ID_DEF_FILE_NAME)
  }

  private def copyDefFile(defFileName: String): Unit = {
    val destPath = RESOURCE_PATH + File.separator + DICT_PATH
    Files.copy(Paths.get(MECAB_KO_DIC_PATH + File.separator + defFileName),
      Paths.get(destPath + File.separator + defFileName), REPLACE_EXISTING)
  }

  private def buildConnectionCostDict(resourcePath: String): Unit = {
    val connectionCostDict = new ConnectionCostDict
    connectionCostDict.loadFromFile(MECAB_KO_DIC_PATH + File.separator + "matrix.def")
    connectionCostDict.save(resourcePath + ConnectionCostDict.resourceConnDicFile)
    println("building connection cost dictionary OK. " +
      s"(${connectionCostDict.getDictionaryInfo()})")
  }

  private def buildLexiconDict(): Unit = {
    val destPath = RESOURCE_PATH + File.separator + DICT_PATH
    val dictPath = new java.io.File(destPath)
    dictPath.mkdirs()

    val lexiconDict = new LexiconDict
    lexiconDict.loadFromDir(MECAB_KO_DIC_PATH)
    lexiconDict.save(destPath + File.separator + TERM_DICT,
                     destPath + File.separator + DICT_MAPPER,
                     destPath + File.separator + TERM_TRIE)
  }

  private def clear(): Unit = {
    val dictPath = Path(RESOURCE_PATH + File.separator + DICT_PATH)
    dictPath.deleteRecursively()
  }
}
