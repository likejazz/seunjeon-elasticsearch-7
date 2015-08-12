package org.bitbucket.eunjeon.seunjeon;

import org.junit.Test;

import java.util.List;

/**
 * Created by parallels on 8/12/15.
 */
public class AnalyzerJavaTest {
    @Test
    public void testParse() {
        List<Term> result = Analyzer.parseJava("버카충했어? 형태소분석기입니다.");
        for (Term term: result) {
            System.out.println("term = " + term);

        }

    }
}
