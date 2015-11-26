package org.bitbucket.eunjeon.seunjeon

object Pos extends Enumeration {
  type Pos = Value
  val BOS, EOS, UNKNOWN, COMPOUND,
      INFLECT_EC, INFLECT_EF, INFLECT_EP, INFLECT_ETM, INFLECT_ETN,
      INFLECT_JC, INFLECT_JKB, INFLECT_JKC, INFLECT_JKG, INFLECT_JKO,
      INFLECT_JKQ, INFLECT_JKS, INFLECT_JKV, INFLECT_JX, INFLECT_XSA,
      INFLECT_XSN, INFLECT_XSV, INFLECT_VCP, INFLECT,
      PREANALYSIS,
      EC, EF, EP, ETM, ETN,
      IC,
      JC, JKB, JKC, JKG, JKO, JKQ, JKS, JKV, JX,
      MAG, MAJ, MM,
      NNG, NNP,
      NNB, NNBC, NP, NR,
      SF, SH, SL, SN, SP,
      SSC, SSO,
      SC, SY, SE,
      VA, VCN, VCP, VV, VX,
      XPN, XR, XSA, XSN, XSV = Value

  val rules = Seq(
    Seq("UNKNOWN","*","*","*","*","*","*","*") -> UNKNOWN,
    Seq("*","*","*","*","Compound","*","*","*") -> COMPOUND,
    Seq("*","*","*","*","Inflect","EC","*","*") -> INFLECT_EC,
    Seq("*","*","*","*","Inflect","EF","*","*") -> INFLECT_EF,
    Seq("*","*","*","*","Inflect","EP","*","*") -> INFLECT_EP,
    Seq("*","*","*","*","Inflect","ETM","*","*") -> INFLECT_ETM,
    Seq("*","*","*","*","Inflect","ETN","*","*") -> INFLECT_ETN,
    Seq("*","*","*","*","Inflect","JC","*","*") -> INFLECT_JC,
    Seq("*","*","*","*","Inflect","JKB","*","*") -> INFLECT_JKB,
    Seq("*","*","*","*","Inflect","JKC","*","*") -> INFLECT_JKC,
    Seq("*","*","*","*","Inflect","JKG","*","*") -> INFLECT_JKG,
    Seq("*","*","*","*","Inflect","JKO","*","*") -> INFLECT_JKO,
    Seq("*","*","*","*","Inflect","JKQ","*","*") -> INFLECT_JKQ,
    Seq("*","*","*","*","Inflect","JKS","*","*") -> INFLECT_JKS,
    Seq("*","*","*","*","Inflect","JKV","*","*") -> INFLECT_JKV,
    Seq("*","*","*","*","Inflect","JX","*","*") -> INFLECT_JX,
    Seq("*","*","*","*","Inflect","XSA","*","*") -> INFLECT_XSA,
    Seq("*","*","*","*","Inflect","XSN","*","*") -> INFLECT_XSN,
    Seq("*","*","*","*","Inflect","XSV","*","*") -> INFLECT_XSV,
    Seq("*","*","*","*","Inflect","VCP","*","*") -> INFLECT_VCP,
    Seq("*","*","*","*","Inflect","*","*","*") -> INFLECT,
    Seq("*","*","*","*","Preanalysis","*","*","*") -> PREANALYSIS,
    Seq("EC","*","*","*","*","*","*","*") -> EC,
    Seq("EF","*","*","*","*","*","*","*") -> EF,
    Seq("EP","*","*","*","*","*","*","*") -> EP,
    Seq("ETM","*","*","*","*","*","*","*") -> ETM,
    Seq("ETN","*","*","*","*","*","*","*") -> ETN,
    Seq("IC","*","*","*","*","*","*","*") -> IC,
    Seq("JC","*","*","*","*","*","*","*") -> JC,
    Seq("JKB","*","*","*","*","*","*","*") -> JKB,
    Seq("JKC","*","*","*","*","*","*","*") -> JKC,
    Seq("JKG","*","*","*","*","*","*","*") -> JKG,
    Seq("JKO","*","*","*","*","*","*","*") -> JKO,
    Seq("JKQ","*","*","*","*","*","*","*") -> JKQ,
    Seq("JKS","*","*","*","*","*","*","*") -> JKS,
    Seq("JKV","*","*","*","*","*","*","*") -> JKV,
    Seq("JX","*","*","*","*","*","*","*") -> JX,
    Seq("MAG","*","*","*","*","*","*","*") -> MAG,
    Seq("MAJ","*","*","*","*","*","*","*") -> MAJ,
    Seq("MM","*","*","*","*","*","*","*") -> MM,
    Seq("NNG","*","*","*","*","*","*","*") -> NNG,
    Seq("NNP","*","*","*","*","*","*","*") -> NNP,
    Seq("NNB","*","*","*","*","*","*","*") -> NNB,
    Seq("NNBC","*","*","*","*","*","*","*") ->NNBC,
    Seq("NP","*","*","*","*","*","*","*") -> NP,
    Seq("NR","*","*","*","*","*","*","*") -> NR,
    Seq("SF","*","*","*","*","*","*","*") -> SF,
    Seq("SH","*","*","*","*","*","*","*") -> SH,
    Seq("SL","*","*","*","*","*","*","*") -> SL,
    Seq("SN","*","*","*","*","*","*","*") -> SN,
    Seq("SP","*","*","*","*","*","*","*") -> SP,
    Seq("SSC","*","*","*","*","*","*","*") -> SSC,
    Seq("SSO","*","*","*","*","*","*","*") -> SSO,
    Seq("SC","*","*","*","*","*","*","*") -> SC,
    Seq("SY","*","*","*","*","*","*","*") -> SY,
    Seq("SE","*","*","*","*","*","*","*") -> SE,
    Seq("VA","*","*","*","*","*","*","*") -> VA,
    Seq("VCN","*","*","*","*","*","*","*") -> VCN,
    Seq("VCP","*","*","*","*","*","*","*") -> VCP,
    Seq("VV","*","*","*","*","*","*","*") -> VV,
    Seq("VX","*","*","*","*","*","*","*") -> VX,
    Seq("XPN","*","*","*","*","*","*","*") -> XPN,
    Seq("XR","*","*","*","*","*","*","*") -> XR,
    Seq("XSA","*","*","*","*","*","*","*") -> XSA,
    Seq("XSN","*","*","*","*","*","*","*") -> XSN,
    Seq("XSV","*","*","*","*","*","*","*") -> XSV
  )

  def apply(feature:Seq[String]): Pos = { try {
      rules.find(rule => isMatch(rule._1, feature)).get._2
    } catch {
      case _: Throwable => println(feature); throw new Exception
    }
  }


  private def isMatch(pattern:Seq[String], feature:Seq[String]): Boolean = {
    for (idx <- pattern.indices) {
      val patternChar = pattern(idx)
      if (patternChar != "*" && patternChar != feature(idx)) return false
    }
    true
  }
}
