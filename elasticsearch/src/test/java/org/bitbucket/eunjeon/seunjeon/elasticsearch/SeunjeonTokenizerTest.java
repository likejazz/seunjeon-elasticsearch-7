package org.bitbucket.eunjeon.seunjeon.elasticsearch;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.*;
import org.bitbucket.eunjeon.seunjeon.Analyzer;
import org.junit.Test;

import java.io.*;

public class SeunjeonTokenizerTest {

    @Test
    public void testTokenize() throws IOException {
        System.out.println(tokenize("영호와김고은"));
        System.out.println(tokenize("전자"));
        System.out.println(tokenize("전자는"));
        System.out.println(tokenize("삼성전자는"));
        System.out.println(tokenize("LG전자는"));
        System.out.println(tokenize("아버지가방에들어가신다."));
        System.out.println(tokenize("무궁화꽃이피었습니다."));
        System.out.println(tokenize("직무를 행한다."));
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

