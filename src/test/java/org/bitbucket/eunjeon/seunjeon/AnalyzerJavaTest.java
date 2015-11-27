package org.bitbucket.eunjeon.seunjeon;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;


public class AnalyzerJavaTest {
    @Test
    public void testParse() {
        List<LatticeNode> result = Analyzer.parseJava("아버지가방에들어가신다.");
        for (LatticeNode term: result) {
            System.out.println(term);
        }
    }

    @Before
    public void setUp() {
        Analyzer.resetUserDict();
    }

    @Test
    public void testUserDictDir() {
        System.out.println("# BEFORE");
        List<LatticeNode> result = Analyzer.parseJava("덕후냄새가 난다.");
        for (LatticeNode term: result) {
            System.out.println(term);
        }
        System.out.println("# BEFORE");
        Analyzer.setUserDictDir("src/test/resources/userdict/");
        result = Analyzer.parseJava("덕후냄새가 난다.");
        for (LatticeNode term: result) {
            System.out.println(term);
        }
    }

    @Test
    public void testUserDict() {
        System.out.println("# BEFORE");
        List<LatticeNode> result = Analyzer.parseJava("덕후냄새가 난다.");
        for (LatticeNode term: result) {
            System.out.println(term);
        }
        System.out.println("# BEFORE");
        Analyzer.setUserDict(Arrays.asList("덕후", "버카충,-100", "낄끼빠빠").iterator());
        result = Analyzer.parseJava("덕후냄새가 난다.");
        for (LatticeNode term: result) {
            System.out.println(term);
        }
    }
}
