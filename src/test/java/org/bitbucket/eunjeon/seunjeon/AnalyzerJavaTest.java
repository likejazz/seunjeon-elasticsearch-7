package org.bitbucket.eunjeon.seunjeon;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;


public class AnalyzerJavaTest {
    @Test
    public void testParse() {
        List<LatticeNode> result = Analyzer.parseJava("형태소분석기입니다. 사랑합니다.");
        for (LatticeNode term: result) {
            System.out.println(term);
        }

    }

    @Test
    public void testUserDictDir() {
        List<LatticeNode> result = Analyzer.parseJava("버카충했어?");
        for (LatticeNode term: result) {
            System.out.println(term);
        }
        Analyzer.setUserDictDir("src/test/resources/userdict/");
        result = Analyzer.parseJava("버카충했어?");
        for (LatticeNode term: result) {
            System.out.println(term);
        }
    }

    @Test
    public void testUserDict() {
        List<LatticeNode> result = Analyzer.parseJava("버카충했어?");
        for (LatticeNode term: result) {
            System.out.println(term);
        }
        Analyzer.setUserDict(Arrays.asList("버카충", "낄끼빠빠").iterator());
        result = Analyzer.parseJava("버카충했어?");
        for (LatticeNode term: result) {
            System.out.println(term);
        }
    }
}
