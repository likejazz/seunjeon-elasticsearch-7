package org.bitbucket.eunjeon.seunjeon;

import org.junit.Test;

import java.util.Arrays;


public class ReadmeJavaTest {
    @Test
    public void testReadme() {
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

        // 압축모드 분석(heap memory 사용 최소화. 속도는 상대적으로 느림. -Xmx512m 이하 추천)
        for (LNode node : CompressedAnalyzer.parseJava("아버지가방에들어가신다.")) {
            System.out.println(node);
        }
    }
}
