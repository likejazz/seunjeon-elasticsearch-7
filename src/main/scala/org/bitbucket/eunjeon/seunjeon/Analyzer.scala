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


trait BasicAnalyzer {
  lazy private[seunjeon] val tokenizer: Tokenizer = {
    val lexiconDict = new LexiconDict().load(false)
    val connectionCostDict = new ConnectionCostDict().load()
    new Tokenizer(lexiconDict, connectionCostDict, false)
  }

  def parse(sentence: String): Iterable[LNode] = parseParagraph(sentence).flatMap(_.nodes)
  def parseParagraph(sentence: String): Iterable[Paragraph] = tokenizer.parseText(sentence, dePreAnalysis=true)
  def parse(sentence: String, preAnalysis: Boolean): Iterable[LNode] = parseParagraph(sentence, preAnalysis).flatMap(_.nodes)
  def parseParagraph(sentence: String, preAnalysis: Boolean): Iterable[Paragraph] = tokenizer.parseText(sentence, preAnalysis)

  def parseJava(sentence: String): java.lang.Iterable[LNode] = parseParagraph(sentence).flatMap(_.nodes).asJava
  def parseJavaParagraph(sentence: String): java.lang.Iterable[Paragraph] = tokenizer.parseTextJava(sentence, dePreAnalysis = true)
  def parseJava(sentence: String, preAnalysis: Boolean): java.lang.Iterable[LNode] = parseParagraph(sentence, preAnalysis).flatMap(_.nodes).asJava
  def parseJavaParagraph(sentence: String, preAnalysis: Boolean): java.lang.Iterable[Paragraph] = tokenizer.parseTextJava(sentence, preAnalysis)

  def setUserDictDir(path: String): Unit =
    tokenizer.setUserDict(new LexiconDict().loadFromDir(path))
  def setUserDictFile(file: String): Unit =
    tokenizer.setUserDict(new LexiconDict().loadFromFile(file))
  def setUserDict(iterator: Iterator[String]): Unit =
    tokenizer.setUserDict(new LexiconDict().loadFromIterator(iterator))
  def setUserDict(iterator: java.util.Iterator[String]): Unit =
    tokenizer.setUserDict(new LexiconDict().loadFromIterator(iterator.asScala))

  def parseEojeol(sentence: String): Iterable[Eojeol] = Eojeoler.build(parseParagraph(sentence)).flatMap(_.eojeols)
  def parseEojeol(paragraphs: Iterable[Paragraph]): Iterable[Eojeol] = Eojeoler.build(paragraphs).flatMap(_.eojeols)
  def parseEojeolParagraph(sentence: String): Iterable[EojeolParagraph] = Eojeoler.build(parseParagraph(sentence))
  def parseEojeolParagraph(paragraphs: Iterable[Paragraph]): Iterable[EojeolParagraph] = Eojeoler.build(paragraphs)

  def parseEojeolJava(sentence: String): java.lang.Iterable[Eojeol] = parseEojeol(sentence).asJava
  def parseEojeolJava(paragraphs: java.lang.Iterable[Paragraph]): java.lang.Iterable[Eojeol] = parseEojeol(paragraphs.asScala).asJava
  def parseEojeolParagraphJava(sentence: String): java.lang.Iterable[EojeolParagraph] = Eojeoler.build(parseParagraph(sentence)).asJava
  def parseEojeolParagraphJava(paragraphs: java.lang.Iterable[Paragraph]): java.lang.Iterable[EojeolParagraph] = parseEojeolParagraph(paragraphs.asScala).asJava

  def resetUserDict(): Unit =
    tokenizer.setUserDict(new LexiconDict().loadFromIterator(Seq[String]().toIterator))

  /**
    * UNKNOWN ???????????? ????????? ?????? ????????? ???????????????. ?????? ?????? ???????????? ??????
    * ????????? ???????????? ?????? ????????? ???????????? ????????????. ????????? "????????????????????????????????????"
    * ????????? length ??? 12??? ????????? ?????? "????????????????????????????????????/UNK" ?????? ???????????????.
    * @param length
    */
  def setMaxUnkLength(length:Int): Unit = tokenizer.setMaxUnkLength(length)
}

object Analyzer extends BasicAnalyzer {}

object CompressedAnalyzer extends BasicAnalyzer {
  override lazy val tokenizer: Tokenizer = {
    val lexiconDict = new LexiconDict().load(true)
    val connectionCostDict = new ConnectionCostDict().load()
    new Tokenizer(lexiconDict, connectionCostDict, true)
  }

}


// TODO: CompressedAnalyzer ?

