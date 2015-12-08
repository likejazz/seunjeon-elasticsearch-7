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

  def parse(sentence: String): Seq[LNode] = tokenizer.parseText(sentence, dePreAnalysis=true)
  def parse(sentence: String, preAnalysis:Boolean): Seq[LNode] = tokenizer.parseText(sentence, preAnalysis)
  def parseJava(sentence: String): java.util.List[LNode] = tokenizer.parseText(sentence, dePreAnalysis=true).asJava
  def parseJava(sentence: String, preAnalysis:Boolean): java.util.List[LNode] = tokenizer.parseText(sentence, preAnalysis).asJava

  def setUserDictDir(path: String): Unit = tokenizer.setUserDict(new LexiconDict().loadFromDir(path))
  def setUserDict(iterator: Iterator[String]): Unit = tokenizer.setUserDict(new LexiconDict().loadFromIterator(iterator))
  def setUserDict(iterator: java.util.Iterator[String]): Unit = tokenizer.setUserDict(new LexiconDict().loadFromIterator(iterator.asScala))

  def parseEojeol(sentence: String): Seq[Eojeol] = Eojeoler.build(parse(sentence), deCompound=false)
  def parseEojeol(lnodes: Seq[LNode]): Seq[Eojeol] = Eojeoler.build(lnodes, deCompound=false)
  def parseEojeol(sentence: String, deCompound: Boolean): Seq[Eojeol] = Eojeoler.build(parse(sentence), deCompound)
  def parseEojeol(lnodes: Seq[LNode], deCompound: Boolean): Seq[Eojeol] = Eojeoler.build(lnodes, deCompound)
  def parseEojeolJava(sentence: String): java.util.List[Eojeol] = Eojeoler.build(parse(sentence), deCompound=false).asJava
  def parseEojeolJava(lnodes: List[LNode]): java.util.List[Eojeol] = Eojeoler.build(lnodes, deCompound=false).asJava
  def parseEojeolJava(sentence: String, deCompound: Boolean): java.util.List[Eojeol] = Eojeoler.build(parse(sentence), deCompound).asJava
  def parseEojeolJava(lnodes: List[LNode], deCompound: Boolean): java.util.List[Eojeol] = Eojeoler.build(lnodes, deCompound).asJava

  def resetUserDict(): Unit = tokenizer.setUserDict(new LexiconDict().loadFromIterator(Seq[String]().toIterator))
}
