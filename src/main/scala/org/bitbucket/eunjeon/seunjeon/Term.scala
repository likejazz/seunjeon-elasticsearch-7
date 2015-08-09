package org.bitbucket.eunjeon.seunjeon

object Term {
  def createUnknownTerm(surface:String): Term = {
    new Term(surface, -1, -1, 500*surface.length, "UNKNOWN")
  }
}

case class Term(surface:String,
                leftId:Short,
                rightId:Short,
                cost:Int,
                feature:String) {
}
