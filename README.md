# seunjeon
[mecab-ko-dic](https://bitbucket.org/eunjeon/mecab-ko-dic) 기반으로 만들어진 JVM 상에서 돌아가는 한국어 형태소분석기입니다. 기본적으로 java와 scala 인터페이스를 제공합니다. 사전이 패키지 내에 포함되어 있기 때문에 별도로 [mecab-ko-dic](https://bitbucket.org/eunjeon/mecab-ko-dic)을 설치할 필요가 없습니다.
특징으로는 (시스템 사전에 등록되어 있는 단어에 한하여) 복합명사 분해와 활용어 원형 찾기가 가능합니다. (속도도 빨라염)

## elasticsearch
[여기](https://bitbucket.org/eunjeon/seunjeon/raw/master/elasticsearch/)를 참고하세요.

## Release
| version | scala(java)          | note          |
|---------|----------------------|---------------|
| 1.4.0   | 2.12(1.8)            | 기능 변화 없음.(변수명 변경 및 클래스 추상화)<br>scala_2.11(jdk_1.7)은 지원하지 않습니다.  |
| 1.3.1   | 2.11(1.7), 2.12(1.8) | 사전 누락으로 인한 오분석 수정, 한자 분석 버그 수정  |
| 1.3.0   | 2.11(1.7), 2.12(1.8) | 사용자 사전에 복합명사 등록 기능 추가  |
| 1.2.0   | 2.11(1.7), 2.12(1.8) | 추가기능 없음 |
| 1.1.1   | 2.10(1.7), 2.11(1.7) |               |


### Maven
```xml
<dependencies>
    <dependency>
        <groupId>org.bitbucket.eunjeon</groupId>
        <artifactId>seunjeon_2.12</artifactId>
        <version>1.4.0</version>
    </dependency>
</dependencies>
```

### SBT
```scala
libraryDependencies += "org.bitbucket.eunjeon" %% "seunjeon" % "1.4.0"
```

### 사용
#### scala
```scala
import org.bitbucket.eunjeon.seunjeon.Analyzer

// 형태소 분석
Analyzer.parse("아버지가방에들어가신다.").foreach(println)

// 어절 분석
Analyzer.parseEojeol("아버지가방에들어가신다.").foreach(println)
// or
Analyzer.parseEojeol(Analyzer.parse("아버지가방에들어가신다.")).foreach(println)

/**
  * 사용자 사전 추가
  * surface,cost
  *   surface: 단어명. '+' 로 복합명사를 구성할 수 있다.
  *           '+'문자 자체를 사전에 등록하기 위해서는 '\+'로 입력. 예를 들어 'C\+\+'
  *   cost: 단어 출연 비용. 작을수록 출연할 확률이 높다.
  */
Analyzer.setUserDict(Seq("덕후", "버카충,-100", "낄끼+빠빠,-100", """C\+\+""").toIterator)
Analyzer.parse("덕후냄새가 난다.").foreach(println)

// 활용어 원형
Analyzer.parse("빨라짐").flatMap(_.deInflect()).foreach(println)

// 복합명사 분해
val ggilggi = Analyzer.parse("낄끼빠빠")
ggilggi.foreach(println)  // 낄끼빠빠
ggilggi.flatMap(_.deCompound()).foreach(println)  // 낄끼+빠빠

Analyzer.parse("C++").flatMap(_.deInflect()).foreach(println) // C++
```
품사태그는 [여기](https://docs.google.com/spreadsheets/d/1-9blXKjtjeKZqsf4NzHeYJCrr49-nXeRF6D80udfcwY/edit#gid=589544265)를 참고하세요.

#### java
```java
import org.bitbucket.eunjeon.seunjeon.Analyzer;

class Smaple {
    public void main(String[] args) {
        // 형태소 분석
        for (LNode node : Analyzer.parseJava("아버지가방에들어가신다.")) {
            System.out.println(node);
        }

        // 어절 분석
        for (Eojeol eojeol: Analyzer.parseEojeolJava("아버지가방에들어가신다.")) {
            System.out.println(eojeol);
            for (LNode node: eojeol.nodesJava()) {
                System.out.println(node);
            }
        }

        /**
         * 사용자 사전 추가
         * surface,cost
         *   surface: 단어명. '+' 로 복합명사를 구성할 수 있다.
         *           '+'문자 자체를 사전에 등록하기 위해서는 '\+'로 입력. 예를 들어 'C\+\+'
         *   cost: 단어 출연 비용. 작을수록 출연할 확률이 높다.
         */
        Analyzer.setUserDict(Arrays.asList("덕후", "버카충,-100", "낄끼+빠빠,-100").iterator());
        for (LNode node : Analyzer.parseJava("덕후냄새가 난다.")) {
            System.out.println(node);
        }

        // 활용어 원형
        for (LNode node : Analyzer.parseJava("빨라짐")) {
            for (LNode node2: node.deInflectJava()) {
                System.out.println(node2);
            }
        }

        // 복합명사 분해
        for (LNode node : Analyzer.parseJava("낄끼빠빠")) {
            System.out.println(node);   // 낄끼빠빠
            for (LNode node2: node.deCompoundJava()) {
                System.out.println(node2);  // 낄끼+빠빠
            }
        }
    }
}
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