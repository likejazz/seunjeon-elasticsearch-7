package org.bitbucket.eunjeon.seunjeon.elasticsearch;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.*;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

public class SeunjeonTokenizerTest {
    @Test
    public void testTokenize() throws IOException {
        Tokenizer tokenizer = new SeunjeonTokenizer();
        tokenizer.setReader(new StringReader("소설 무궁화꽃이 피었습니다."));
        tokenizer.reset();
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
                    offsetAttr.endOffset()  + ":" +
                    typeAttr.type();
            System.out.println(toString);
        }

    }
}
