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

import scala.collection.JavaConverters._

object Analyzer {
  val tokenizer:Tokenizer = initTokenizer()

  private def initTokenizer(): Tokenizer = {
    val lexiconDict = new LexiconDict().load()
    val connectionCostDict = new ConnectionCostDict().load()

    new Tokenizer(lexiconDict, connectionCostDict)
  }

  def parse(sentence: String): Seq[TermNode] = {
    tokenizer.parseText(sentence)
  }

  def parseJava(sentence: String): java.util.List[TermNode] = {
    tokenizer.parseText(sentence).asJava
  }

  def setUserDictDir(path: String): Unit = {
    tokenizer.setUserDict(new LexiconDict().loadFromDir(path))
  }

  def setUserDict(iterator: Iterator[String]): Unit = {
    tokenizer.setUserDict(new LexiconDict().loadFromIterator(iterator))
  }

  def setUserDict(iterator: java.util.Iterator[String]): Unit = {
    tokenizer.setUserDict(new LexiconDict().loadFromIterator(iterator.asScala))
  }
}
