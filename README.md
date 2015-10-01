# seunjeon
[mecab-ko-dic](https://bitbucket.org/eunjeon/mecab-ko-dic) 기반으로 만들어진 JVM 상에서 돌아가는 한국어 형태소분석기입니다. 기본적으로 java와 scala 인터페이스를 제공합니다. 사전이 패키지 내에 포함되어 있기 때문에 별도로 [mecab-ko-dic](https://bitbucket.org/eunjeon/mecab-ko-dic)을 설치할 필요가 없습니다.

## Maven
### 배포버전
```xml
<dependencies>
    <dependency>
        <groupId>org.bitbucket.eunjeon</groupId>
        <artifactId>seunjeon_2.11</artifactId>
        <version>0.3.0</version>
    </dependency>
</dependencies>
```

### 개발버전
```xml
<dependencies>
    <dependency>
        <groupId>org.bitbucket.eunjeon</groupId>
        <artifactId>seunjeon_2.11</artifactId>
        <version>0.3.0-SNAPSHOT</version>
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

## 형태소분석하기
### java
```java
List<TermNode> result = Analyzer.parseJava("아버지가방에들어가신다.");
for (TermNode term: result) {
    System.out.println(term);
}

```
### scala
```scala
Analyzer.parse("아버지가방에들어가신다.").foreach(println)
```
### 결과
```bash
TermNode(Term(BOS,0,0,0,BOS),0,0,0)
TermNode(Term(아버지,1784,3536,2818,NNG,*,F,아버지,*,*,*,*),0,2,-1135)
TermNode(Term(가,490,1044,1501,JKS,*,F,가,*,*,*,*),3,3,-738)
TermNode(Term(방,1784,3537,2975,NNG,*,T,방,*,*,*,*),4,4,660)
TermNode(Term(에,356,307,1248,JKB,*,F,에,*,*,*,*),5,5,203)
TermNode(Term(들어가,2421,3574,1648,VV,*,F,들어가,*,*,*,*),6,8,583)
TermNode(Term(신다,5,6,3600,EP+EF,*,F,신다,Inflect,EP,EF,시/EP/*+ᆫ다/EF/*),9,10,-1256)
TermNode(Term(.,1794,3555,3559,SF,*,*,*,*,*,*,*),11,11,325)
TermNode(Term(EOS,0,0,0,EOS),12,12,2102)
```
품사태그는 [여기](https://docs.google.com/spreadsheets/d/1-9blXKjtjeKZqsf4NzHeYJCrr49-nXeRF6D80udfcwY/edit#gid=589544265)를 참고하세요.

## 사용자 정의 사전
### scala
#### 파일에서 읽기
특정 디렉토리에 csv 확장자로 파일들을 만듭니다. (*.csv)
```text
# surface,cost
#   surface: 단어
#   cost: 단어 출연 비용. 작을수록 출연할 확률이 높다.
버카충,-100
어그로
```
```scala
println("# BEFORE")
Analyzer.parse("버카충했어?").foreach(println)
Analyzer.setUserDictDir("userdict/")
println("# AFTER ")
Analyzer.parse("버카충했어?").foreach(println)
```
csv 파일이 있는 디렉토리를 명시하여 로딩합니다.
#### iterator 에서 읽기
```scala
println("# BEFORE")
Analyzer.parse("버카충했어?").foreach(println)
Analyzer.setUserDict(Seq("버카충,-100", "낄끼빠빠").toIterator)
println("# AFTER ")
Analyzer.parse("버카충했어?").foreach(println)
```

### java
#### 파일에서 읽기
특정 디렉토리에 csv 확장자로 파일들을 만듭니다. (*.csv)
```text
# surface,cost
#   surface: 단어
#   cost: 단어 출연 비용. 작을수록 출연할 확률이 높다.
버카충,-100
어그로
```
```java
System.out.println("# BEFORE");
List<TermNode> result = Analyzer.parseJava("버카충했어?");
for (TermNode term: result) {
    System.out.println(term);
}
System.out.println("# BEFORE");
Analyzer.setUserDictDir("src/test/resources/userdict/");
result = Analyzer.parseJava("버카충했어?");
for (TermNode term: result) {
    System.out.println(term);
}
```
#### iterator에서 읽기
```java
System.out.println("# BEFORE");
List<TermNode> result = Analyzer.parseJava("버카충했어?");
for (TermNode term: result) {
    System.out.println(term);
}
System.out.println("# BEFORE");
Analyzer.setUserDict(Arrays.asList("버카충", "낄끼빠빠").iterator());
result = Analyzer.parseJava("버카충했어?");
for (TermNode term: result) {
    System.out.println(term);
}
```

### 결과
```bash
# BEFORE
TermNode(Term(BOS,0,0,0,BOS),0,0,0)
TermNode(Term(버,1788,3544,6797,NNP,인명,F,버,*,*,*,*),0,0,-2213)
TermNode(Term(카,1784,3536,4644,NNG,*,F,카,*,*,*,*),1,1,4669)
TermNode(Term(충,1784,3537,4422,NNG,*,T,충,*,*,*,*),2,2,9509)
TermNode(Term(했,2693,9,-30,XSV+EP,*,T,했,Inflect,XSV,EP,하/XSV/*+았/EP/*),3,3,11409)
TermNode(Term(어,4,6,2409,EF,*,F,어,*,*,*,*),4,4,8935)
TermNode(Term(?,1794,3555,3559,SF,*,*,*,*,*,*,*),5,5,9325)
TermNode(Term(EOS,0,0,0,EOS),6,6,11102)
# AFTER
TermNode(Term(BOS,0,0,0,BOS),0,0,0)
TermNode(Term(버카충,1784,3537,700,NNG,*,T),0,2,-1135)
TermNode(Term(했,2693,9,-30,XSV+EP,*,T,했,Inflect,XSV,EP,하/XSV/*+았/EP/*),3,3,-2957)
TermNode(Term(어,4,6,2409,EF,*,F,어,*,*,*,*),4,4,-5431)
TermNode(Term(?,1794,3555,3559,SF,*,*,*,*,*,*,*),5,5,-5041)
TermNode(Term(EOS,0,0,0,EOS),6,6,-3264)
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
