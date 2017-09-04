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
  lazy val tokenizer:Tokenizer = initTokenizer()

  def initTokenizer(): Tokenizer = {
    val lexiconDict = new LexiconDict().load()
    val connectionCostDict = new ConnectionCostDict().load()
    new Tokenizer(lexiconDict, connectionCostDict)
  }

  def parse(sentence: String): Seq[LNode] = tokenizer.parseText(sentence, dePreAnalysis=true)
  def parse(sentence: String, preAnalysis:Boolean): Seq[LNode] = tokenizer.parseText(sentence, preAnalysis)
  def parseJava(sentence: String): java.util.List[LNode] = tokenizer.parseText(sentence, dePreAnalysis=true).asJava
  def parseJava(sentence: String, preAnalysis:Boolean): java.util.List[LNode] = tokenizer.parseText(sentence, preAnalysis).asJava

  def setUserDictDir(path: String): Unit = tokenizer.setUserDict(new LexiconDict().loadFromDir(path))
  def setUserDictFile(file: String): Unit = tokenizer.setUserDict(new LexiconDict().loadFromFile(file))
  def setUserDict(iterator: Iterator[String]): Unit = tokenizer.setUserDict(new LexiconDict().loadFromIterator(iterator))
  def setUserDict(iterator: java.util.Iterator[String]): Unit = tokenizer.setUserDict(new LexiconDict().loadFromIterator(iterator.asScala))

  def parseEojeol(sentence: String): Seq[Eojeol] = Eojeoler.build(parse(sentence))
  def parseEojeol(lnodes: Seq[LNode]): Seq[Eojeol] = Eojeoler.build(lnodes)
  def parseEojeolJava(sentence: String): java.util.List[Eojeol] = Eojeoler.build(parse(sentence)).asJava
  def parseEojeolJava(lnodes: java.util.List[LNode]): java.util.List[Eojeol] = Eojeoler.build(lnodes.asScala).asJava

  def resetUserDict(): Unit = tokenizer.setUserDict(new LexiconDict().loadFromIterator(Seq[String]().toIterator))

  /**
    * UNKNOWN 키워드로 생성할 최대 길이를 설정합니다. 너무 길면 띄어쓰기 없이
    * 사용된 문장에서 잘못 분석될 가능성이 높습니다. 입력이 "농어촌체험휴양하누리마을"
    * 이면서 length 가 12로 설정될 경우 "농어촌체험휴양하누리마을/UNK" 으로 분석됩니다.
    * @param length
    */
  def setMaxUnkLength(length:Int) = tokenizer.setMaxUnkLength(length)
}
