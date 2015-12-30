package org.bitbucket.eunjeon.seunjeon.elasticsearch;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.*;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class SeunjeonTokenizerTest {

    @Test
    public void testTokenize() throws IOException {
        assertEquals("eojeol",
                "유영호/N:1:1:0:3:N;유영호와:0:1:0:4:EOJEOL;이용운/N:1:1:4:7:N;", tokenize("유영호와이용운"));
        assertEquals("compound + eojeol",
                "삼성/N:1:1:0:2:N;삼성전자는:0:2:0:5:EOJEOL;전자/N:1:1:2:4:N;", tokenize("삼성전자는"));
        assertEquals("noun noun eojeol",
                "LG/SL:1:1:0:2:SL;전자/N:1:1:2:4:N;전자는:0:1:2:5:EOJEOL;", tokenize("LG전자는"));
        assertEquals("deInflect",
                "직무/N:1:1:0:2:N;직무를:0:1:0:3:EOJEOL;행하/V:1:1:4:6:V;행한다:0:1:4:7:EOJEOL;", tokenize("직무를 행한다."));
        assertEquals("number, symbol",
                "55/SN:1:1:0:2:SN;32/SN:1:1:3:5:SN;ms/SL:1:1:5:7:SL;", tokenize("55.32ms"));

        System.out.println(tokenize("들어가신다."));
        System.out.println(tokenize("아버지가방에들어가신다."));
        System.out.println(tokenize("무궁화꽃이피었습니다."));
    }

    @Test
    public void testPerformance() throws IOException {
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
        result = tokenize(longContents);
        System.out.println("result = " + result);
        long startTime = System.nanoTime();
        int times = 1000;
        for (int i = 0; i < times; i++) {
            result = tokenize(longContents);
        }
        long endTime = System.nanoTime();
        long elapsedTime = (endTime - startTime) / times;
        System.out.println("result = " + result);
        System.out.println("elapsedTime = " + elapsedTime + " us");
    }

    public String tokenize(String document) throws IOException {
        Tokenizer tokenizer = new SeunjeonTokenizer();
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
        return sb.toString();

    }
}

