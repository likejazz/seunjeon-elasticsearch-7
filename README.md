# seunjeon
[mecab-ko-dic](https://bitbucket.org/eunjeon/mecab-ko-dic) 기반으로 만들어진 JVM 상에서 돌아가는 한국어 형태소분석기입니다. 기본적으로 java와 scala 인터페이스를 제공합니다. 사전이 패키지 내에 포함되어 있기 때문에 별도로 [mecab-ko-dic](https://bitbucket.org/eunjeon/mecab-ko-dic)을 설치할 필요가 없습니다.

[TOC]

## Maven
### 배포버전
```xml
<dependencies>
    <dependency>
        <groupId>org.bitbucket.eunjeon</groupId>
        <artifactId>seunjeon_2.11</artifactId>
        <version>0.7.0</version>
    </dependency>
</dependencies>
```

### 개발버전
```xml
<dependencies>
    <dependency>
        <groupId>org.bitbucket.eunjeon</groupId>
        <artifactId>seunjeon_2.11</artifactId>
        <version>0.7.0-SNAPSHOT</version>
    </dependency>
</dependencies>

<repositories>
    <repository>
        <id>oss-sonatype</id>
        <name>oss-sonatype</name>
        <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

## 형태소 분석하기
### java
```java
public void testParse() {
    List<LNode> result = Analyzer.parseJava("아버지가방에들어가신다.");
    for (LNode node: result) {
        System.out.println(node);
    }
}
```
### scala
```scala
Analyzer.parse("아버지가방에들어가신다.").foreach(println)
```
### 결과
```bash
LNode(Morpheme(BOS,0,0,0,WrappedArray(BOS),WrappedArray(BOS)),0,0,0)
LNode(Morpheme(아버지,1784,3536,2818,WrappedArray(NNG, *, F, 아버지, *, *, *, *),WrappedArray(N)),0,2,-1135)
LNode(Morpheme(가,490,1044,1501,WrappedArray(JKS, *, F, 가, *, *, *, *),WrappedArray(J)),3,3,-738)
LNode(Morpheme(방,1784,3537,2975,WrappedArray(NNG, *, T, 방, *, *, *, *),WrappedArray(N)),4,4,660)
LNode(Morpheme(에,356,307,1248,WrappedArray(JKB, *, F, 에, *, *, *, *),WrappedArray(J)),5,5,203)
LNode(Morpheme(들어가,2421,3574,1648,WrappedArray(VV, *, F, 들어가, *, *, *, *),WrappedArray(V)),6,8,583)
LNode(Morpheme(신다,5,6,3600,WrappedArray(EP+EF, *, F, 신다, Inflect, EP, EF, 시/EP/*+ᆫ다/EF/*),WrappedArray(EP, E)),9,10,-1256)
LNode(Morpheme(.,1794,3555,3559,WrappedArray(SF, *, *, *, *, *, *, *),WrappedArray(S)),11,11,325)
LNode(Morpheme(EOS,0,0,0,WrappedArray(EOS),WrappedArray(BOS)),12,12,2102)
```
품사태그는 [여기](https://docs.google.com/spreadsheets/d/1-9blXKjtjeKZqsf4NzHeYJCrr49-nXeRF6D80udfcwY/edit#gid=589544265)를 참고하세요.

## 어절 분석하기
### scala
```scala
Analyzer.parseEojeol("아버지가방에들어가신다.").map(_.surface).foreach(println)
// 또는
Analyzer.parseEojeol(Analyzer.parse("아버지가방에들어가신다.")).map(_.surface).foreach(println)
```
### java
```java
List<Eojeol> eojeols = Analyzer.parseEojeolJava("아버지가방에들어가신다.");
for (Eojeol eojeol: eojeols) {
    System.out.println(eojeol.surface());
}
```
### 결과
```bash
BOS
아버지가
방에
들어가신다
.
EOS
```

## 사용자 정의 사전
### scala
#### 파일에서 읽기
특정 디렉토리에 csv 확장자로 파일들을 만듭니다. (*.csv)
```text
# surface,cost
#   surface: 단어
#   cost: 단어 출연 비용. 작을수록 출연할 확률이 높다.
덕후
버카충,-100
어그로
```
```scala
println("# BEFORE")
Analyzer.parse("덕후냄새가 난다.").foreach(println)
Analyzer.setUserDictDir("userdict/")
println("# AFTER ")
Analyzer.parse("덕후냄새가 난다.").foreach(println)
```
csv 파일이 있는 디렉토리를 명시하여 로딩합니다.
#### iterator 에서 읽기
```scala
println("# BEFORE")
Analyzer.parse("").foreach(println)
Analyzer.setUserDict(Seq("덕후", "버카충,-100", "낄끼빠빠").toIterator)
println("# AFTER ")
Analyzer.parse("").foreach(println)
```

### java
#### 파일에서 읽기
특정 디렉토리에 csv 확장자로 파일들을 만듭니다. (*.csv)
```text
# surface,cost
#   surface: 단어
#   cost: 단어 출연 비용. 작을수록 출연할 확률이 높다.
덕후
버카충,-100
어그로
```
```java
System.out.println("# BEFORE");
List<LNode> result = Analyzer.parseJava("덕후냄새가 난다.");
for (LNode term: result) {
    System.out.println(term);
}
System.out.println("# BEFORE");
Analyzer.setUserDictDir("src/test/resources/userdict/");
result = Analyzer.parseJava("덕후냄새가 난다.");
for (LNode term: result) {
    System.out.println(term);
}
```
#### iterator에서 읽기
```java
System.out.println("# BEFORE");
List<LNode> result = Analyzer.parseJava("덕후냄새가 난다.");
for (LNode term: result) {
    System.out.println(term);
}
System.out.println("# BEFORE");
Analyzer.setUserDict(Arrays.asList("덕후", "버카충,-100", "낄끼빠빠").iterator());
result = Analyzer.parseJava("덕후냄새가 난다.");
for (LNode term: result) {
    System.out.println(term);
}
```

### 결과
```bash
# BEFORE
LNode(Morpheme(BOS,0,0,0,WrappedArray(BOS),WrappedArray(BOS)),0,0,0)
LNode(Morpheme(덕,1784,3537,3128,WrappedArray(NNG, *, T, 덕, *, *, *, *),WrappedArray(N)),0,0,-1135)
LNode(Morpheme(후,1784,3536,2200,WrappedArray(NNG, *, F, 후, *, *, *, *),WrappedArray(N)),1,1,2189)
LNode(Morpheme(냄새,1784,3536,2123,WrappedArray(NNG, *, F, 냄새, *, *, *, *),WrappedArray(N)),2,3,4585)
LNode(Morpheme(가,490,1044,1501,WrappedArray(JKS, *, F, 가, *, *, *, *),WrappedArray(J)),4,4,4287)
LNode(Morpheme(난다,2421,6,1277,WrappedArray(VV+EF, *, F, 난다, Inflect, VV, EF, 나/VV/*+ᆫ다/EF/*),WrappedArray(V, E)),6,7,5072)
LNode(Morpheme(.,1794,3555,3559,WrappedArray(SF, *, *, *, *, *, *, *),WrappedArray(S)),8,8,4330)
LNode(Morpheme(EOS,0,0,0,WrappedArray(EOS),WrappedArray(BOS)),9,9,6107)
# AFTER 
LNode(Morpheme(BOS,0,0,0,WrappedArray(BOS),WrappedArray(BOS)),0,0,0)
LNode(Morpheme(덕후,1784,3535,800,WrappedArray(NNG, *, F, 덕후, *, *, *, *),WrappedArray(N)),0,1,-1135)
LNode(Morpheme(냄새,1784,3536,2123,WrappedArray(NNG, *, F, 냄새, *, *, *, *),WrappedArray(N)),2,3,-139)
LNode(Morpheme(가,490,1044,1501,WrappedArray(JKS, *, F, 가, *, *, *, *),WrappedArray(J)),4,4,-437)
LNode(Morpheme(난다,2421,6,1277,WrappedArray(VV+EF, *, F, 난다, Inflect, VV, EF, 나/VV/*+ᆫ다/EF/*),WrappedArray(V, E)),6,7,348)
LNode(Morpheme(.,1794,3555,3559,WrappedArray(SF, *, *, *, *, *, *, *),WrappedArray(S)),8,8,-394)
LNode(Morpheme(EOS,0,0,0,WrappedArray(EOS),WrappedArray(BOS)),9,9,1383)
```

## Group
[https://groups.google.com/forum/#!forum/eunjeon](https://groups.google.com/forum/#!forum/eunjeon) 질문과 개발 참여 환영합니다.

## 형태소분석기 개발
```sh
# 사전 다운로드
./scripts/download-dict.sh mecab-ko-dic-2.0.1-20150920

# 사전 빌드(mecab-ko-dic/* -> src/main/resources/*.dat)
sbt -J-Xmx2G "run-main org.bitbucket.eunjeon.seunjeon.DictBuilder"

# jar 생성
sbt package
```

## License
Copyright 2015 유영호, 이용운. 아파치 라이센스 2.0에 따라 소프트웨어를 사용, 재배포 할 수 있습니다. 더 자세한 사항은 http://www.apache.org/licenses/LICENSE-2.0 을 참조하시기 바랍니다.
