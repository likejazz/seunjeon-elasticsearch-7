package org.bitbucket.eunjeon.seunjeon;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;


public class AnalyzerJavaTest {
    @Test
    public void testParse() {
        List<TermNode> result = Analyzer.parseJava("아버지가방에들어가신다.");
        for (TermNode term: result) {
            System.out.println(term);
        }
    }

    @Test
    public void testUserDictDir() {
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
    }

    @Test
    public void testUserDict() {
        System.out.println("# BEFORE");
        List<TermNode> result = Analyzer.parseJava("버카충했어?");
        for (TermNode term: result) {
            System.out.println(term);
        }
        System.out.println("# AFTER");
        Analyzer.setUserDict(Arrays.asList("버카충", "낄끼빠빠").iterator());
        result = Analyzer.parseJava("버카충했어?");
        for (TermNode term: result) {
            System.out.println(term);
        }
    }
}
