# seunjeon
세종말뭉치 학습 기반은 형태소분석기

순수 JVM에서 실행되도록 만들었습니다.

java
```
List<Term> result = Analyzer.parseJava("버카충했어? 형태소분석기입니다.");
for (Term term: result) {
    System.out.println("term = " + term);

}
```

scala
```
Analyzer.parse("버카충했어? 형태소분석기입니다.").foreach { t: Term =>
  println(t)
}
```
이하 작성중..