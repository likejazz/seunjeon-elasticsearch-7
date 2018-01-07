package org.bitbucket.eunjeon.seunjeon.elasticsearch;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class SeunjeonTokenizerTest {

    @Before
    public void setUp() {
    }

    @Test
    public void testTokenize() throws IOException {
        Tokenizer t = new SeunjeonTokenizer(TokenizerOptions.create("eojeol"));
        assertEquals("eojeol",
                "유영호/N:1:1:0:3:N;유영호와/EOJ:0:1:0:4:EOJ;이용운/N:1:1:4:7:N;", tokenize("유영호와이용운", t));
        assertEquals("compound + eojeol",
                "삼성/N:1:1:0:2:N;삼성전자는/EOJ:0:2:0:5:EOJ;전자/N:1:1:2:4:N;", tokenize("삼성전자는", t));
        assertEquals("noun noun eojeol",
                "lg/SL:1:1:0:2:SL;전자/N:1:1:2:4:N;전자는/EOJ:0:1:2:5:EOJ;", tokenize("LG전자는", t));
        assertEquals("number, symbol",
                "55/SN:1:1:0:2:SN;32/SN:1:1:3:5:SN;ms/SL:1:1:5:7:SL;", tokenize("55.32ms", t));
    }
    @Test
    public void testTokenize2() throws IOException {
        Tokenizer t = new SeunjeonTokenizer(TokenizerOptions.create("eojeol"));
        assertEquals("deInflect",
                "직무/N:1:1:0:2:N;직무를/EOJ:0:1:0:3:EOJ;행하/V:1:1:4:6:V;행한다/EOJ:0:1:4:7:EOJ;", tokenize("직무를 행한다.", t));
    }

    @Test
    public void testUserWords() throws IOException {
        Tokenizer t = new SeunjeonTokenizer(TokenizerOptions.create("user-dict"));
        assertEquals("user words",
                "버카충/UNK:1:1:0:3:UNK;", tokenize("버카충", t));

        Tokenizer ut = new SeunjeonTokenizer(TokenizerOptions.create("user-dict").setUserWords(Collections.singletonList("버카충")));
        assertEquals("user words",
                "버카충/N:1:1:0:3:N;", tokenize("버카충", ut));
    }

    @Test
    public void testDeCompound() throws IOException {
        assertEquals("삼성/N:1:1:0:2:N;삼성전자/EOJ:0:2:0:4:EOJ;전자/N:1:1:2:4:N;",
                tokenize("삼성전자", new SeunjeonTokenizer(TokenizerOptions.create(""))));

        assertEquals("삼성전자/N:1:1:0:4:N;",
                tokenize("삼성전자", new SeunjeonTokenizer(TokenizerOptions.create("").setDeCompound(false))));
    }

    @Test
    public void testDeInflect() throws IOException {
        SeunjeonTokenizer deflectTokenizer =
                new SeunjeonTokenizer(TokenizerOptions.create("").
                        setDeInflect(true).
                        setIndexEojeol(false).
                        setIndexPoses(TokenizerHelper.ALL_POSES_JAVA()));

        SeunjeonTokenizer nonDeflectTokenizer =
                new SeunjeonTokenizer(TokenizerOptions.create("").
                        setDeInflect(false).
                        setIndexEojeol(false).
                        setIndexPoses(TokenizerHelper.ALL_POSES_JAVA()));
        assertEquals("빠르/V:1:1:0:2:V;빨라짐/EOJ:0:2:0:3:EOJ;지/V:1:1:2:3:V;",
                tokenize("빨라짐", new SeunjeonTokenizer(TokenizerOptions.create(""))));

        assertEquals("슬프/V:1:1:0:2:V;ᆫ/E:1:1:1:2:E;", tokenize("슬픈", deflectTokenizer));
        assertEquals("슬픈/V+E:1:1:0:2:V+E;", tokenize("슬픈", nonDeflectTokenizer));

        assertEquals("새롭/V:1:1:0:2:V;ᆫ/E:1:1:1:2:E;사전/N:1:1:4:6:N;생성/N:1:1:7:9:N;",
                tokenize("새로운 사전 생성", deflectTokenizer));
        assertEquals("새로운/V+E:1:1:0:3:V+E;사전/N:1:1:4:6:N;생성/N:1:1:7:9:N;",
                tokenize("새로운 사전 생성", nonDeflectTokenizer));
    }

    @Test
    public void invalidCompoundFeature() throws IOException {
        // TODO: seunjeon project test 로 가야 함
        // ex) 데스노트,Compound,*,*,/NNG/*+노트/NNG/*
        assertEquals("데스노트/N:1:1:0:4:N;",
                tokenize("데스노트", new SeunjeonTokenizer(TokenizerOptions.create(""))));
    }

    @Test
    public void testIndexPoses() throws IOException {
        assertEquals("꽃/N:1:1:0:1:N;꽃이/EOJ:0:1:0:2:EOJ;피/V:1:1:2:3:V;피다/EOJ:0:1:2:4:EOJ;",
                tokenize("꽃이피다", new SeunjeonTokenizer(TokenizerOptions.create(""))));

        assertEquals("꽃/N:1:1:0:1:N;",
                tokenize("꽃이피다", new SeunjeonTokenizer(TokenizerOptions.create("").
                        setIndexPoses(Arrays.asList("N")).
                        setIndexEojeol(false))));

        assertEquals("새롭/V:1:1:0:2:V;사전/N:1:1:4:6:N;생성/N:1:1:7:9:N;",
                tokenize("새로운 사전 생성", new SeunjeonTokenizer(TokenizerOptions.create("").
                        setIndexPoses(Arrays.asList("V", "N")).
                        setDeCompound(true).
                        setDeInflect(true).
                        setIndexEojeol(false))));

        assertEquals("왕/N:1:1:0:1:N;딸/N:1:1:3:4:N;",
                tokenize("왕의 딸로 태어났다고 합니다", new SeunjeonTokenizer(TokenizerOptions.create("").
                        setIndexPoses(Arrays.asList("N")).
                        setDeInflect(true).
                        setIndexEojeol(false))));
    }

    @Test
    public void testLowerCase() throws IOException {
        assertEquals("lg/SL:1:1:0:2:SL;전자/N:1:1:2:4:N;",
                tokenize("LG전자", new SeunjeonTokenizer(TokenizerOptions.create(""))));
    }

    @Test
    public void testEojeol() throws IOException {
        assertEquals("비리조트/EOJ:1:1:0:4:EOJ;리조트/N:0:1:1:4:N;",
                tokenize("비리조트", new SeunjeonTokenizer(TokenizerOptions.create("").setIndexEojeol(true))));
        assertEquals("삼성/N:1:1:0:2:N;삼성전자/EOJ:0:2:0:4:EOJ;전자/N:1:1:2:4:N;",
                tokenize("삼성전자", new SeunjeonTokenizer(TokenizerOptions.create("").setIndexEojeol(true))));
    }

    @Test
    public void testMaxUnkLength() throws IOException {
        assertNotEquals("농어촌체험휴양하누리마을/UNK:1:1:0:12:UNK;",
                tokenize("농어촌체험휴양하누리마을", new SeunjeonTokenizer(TokenizerOptions.create(""))));
        assertEquals("농어촌체험휴양하누리마을/UNK:1:1:0:12:UNK;",
                tokenize("농어촌체험휴양하누리마을", new SeunjeonTokenizer(TokenizerOptions.create("").setMaxUnkLength(20))));
    }

    @Ignore
    public void testPerformance() throws IOException {
        Tokenizer t = new SeunjeonTokenizer(TokenizerOptions.create(""));
        ClassLoader classLoader = getClass().getClassLoader();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(classLoader.getResourceAsStream("long_contents.txt")));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        reader.close();
        String longContents = sb.toString();
        System.out.println("longContents = " + longContents);
        String result = null;
        result = tokenize(longContents, t);
        System.out.println("result = " + result);
        long startTime = System.nanoTime();
        int times = 1000;
        for (int i = 0; i < times; i++) {
            result = tokenize(longContents, t);
        }
        long endTime = System.nanoTime();
        long elapsedTime = (endTime - startTime) / times;
        System.out.println("result = " + result);
        System.out.println("elapsedTime = " + elapsedTime + " us");
    }

    public String tokenize(String document, Tokenizer tokenizer) throws IOException {
        tokenizer.setReader(new StringReader(document));
        tokenizer.reset();
        StringBuilder sb = new StringBuilder();
        while (tokenizer.incrementToken()) {
            CharTermAttribute termAttr = tokenizer.getAttribute(CharTermAttribute.class);
            PositionIncrementAttribute posIncrAttr = tokenizer.getAttribute(PositionIncrementAttribute.class);
            PositionLengthAttribute posLength = tokenizer.getAttribute(PositionLengthAttribute.class);
            OffsetAttribute offsetAttr = tokenizer.getAttribute(OffsetAttribute.class);
            TypeAttribute typeAttr = tokenizer.getAttribute(TypeAttribute.class);
            String toString = new String(termAttr.buffer(), 0, termAttr.length()) + ":" +
                    posIncrAttr.getPositionIncrement() + ":" +
                    posLength.getPositionLength() + ":" +
                    offsetAttr.startOffset() + ":" +
                    offsetAttr.endOffset() + ":" +
                    typeAttr.type();
            sb.append(toString);
            sb.append(";");
        }
        tokenizer.close();
        return sb.toString();

    }
}

