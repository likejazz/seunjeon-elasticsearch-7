# seunjeon
[mecab-ko-dic](https://bitbucket.org/eunjeon/mecab-ko-dic) 기반으로 만들어진 JVM 상에서 돌아가는 한국어 형태소분석기입니다. 기본적으로 java와 scala 인터페이스를 제공합니다. 사전이 패키지 내에 포함되어 있기 때문에 별도로 [mecab-ko-dic](https://bitbucket.org/eunjeon/mecab-ko-dic)을 설치할 필요가 없습니다.

## 설치
  * jdk1.7 에서 컴파일되었습니다.
## Maven
```xml
<dependencies>
    <dependency>
        <groupId>org.bitbucket.eunjeon</groupId>
        <artifactId>seunjeon_2.11</artifactId>
        <version>1.0.3</version>
    </dependency>
</dependencies>
```

## SBT
```scala
libraryDependencies += "org.bitbucket.eunjeon" %% "seunjeon" % "1.0.3"
```
 * Scala 1.10, Scala 1.11

## 사용
### scala
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
  *   surface: 단어
  *   cost: 단어 출연 비용. 작을수록 출연할 확률이 높다.
  */
Analyzer.setUserDict(Seq("덕후", "버카충,-100", "낄끼빠빠").toIterator)
Analyzer.parse("덕후냄새가 난다.").foreach(println)

// 활용어 원형
Analyzer.parse("슬픈").flatMap(_.deInflect()).foreach(println)

// 복합명사 분해
Analyzer.parse("삼성전자").flatMap(_.deCompound()).foreach(println)
```
품사태그는 [여기](https://docs.google.com/spreadsheets/d/1-9blXKjtjeKZqsf4NzHeYJCrr49-nXeRF6D80udfcwY/edit#gid=589544265)를 참고하세요.

### java
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
         *   surface: 단어
         *   cost: 단어 출연 비용. 작을수록 출연할 확률이 높다.
         */
        Analyzer.setUserDict(Arrays.asList("덕후", "버카충,-100", "낄끼빠빠").iterator());
        for (LNode node : Analyzer.parseJava("덕후냄새가 난다.")) {
            System.out.println(node);
        }

        // 활용어 원형
        for (LNode node : Analyzer.parseJava("슬픈")) {
            for (LNode node2: node.deInflectJava()) {
                System.out.println(node2);
            }
        }

        // 복합명사 분해
        for (LNode node : Analyzer.parseJava("삼성전자")) {
            for (LNode node2: node.deCompoundJava()) {
                System.out.println(node2);
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