package org.bitbucket.eunjeon.seunjeon.elasticsearch;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.*;
import org.apache.lucene.util.AttributeFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.Queue;


public class SeunjeonTokenizer extends Tokenizer {
    private CharTermAttribute charTermAtt;
    private PositionIncrementAttribute posIncrAtt;
    private PositionLengthAttribute posLenAtt;
    private OffsetAttribute offsetAtt;
    private TypeAttribute typeAtt;
    private Queue<LuceneToken> tokensQueue;


    public SeunjeonTokenizer() {
        super(AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY);
        charTermAtt = addAttribute(CharTermAttribute.class);
        posIncrAtt = addAttribute(PositionIncrementAttribute.class);
        posLenAtt = addAttribute(PositionLengthAttribute.class);
        offsetAtt = addAttribute(OffsetAttribute.class);
        typeAtt = addAttribute(TypeAttribute.class);
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        tokensQueue = new LinkedList(TokenBuilder.tokenize(getDocument()));
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (tokensQueue.isEmpty()) {
            return false;
        } else {
            LuceneToken pos = tokensQueue.poll();
            posIncrAtt.setPositionIncrement(pos.positionIncr());
            posLenAtt.setPositionLength(pos.positionLength());
            offsetAtt.setOffset(
                    correctOffset(pos.startOffset()),
                    correctOffset(pos.endOffset()));
            String term = pos.surface();
            charTermAtt.copyBuffer(term.toCharArray(), 0, term.length());
            typeAtt.setType(pos.poses());

            return true;
        }
    }

    private String getDocument() throws IOException {
        StringWriter sw = new StringWriter();
        char[] buffer = new char[1024 * 4];
        int n;
        while (-1 != (n = input.read(buffer))) {
            sw.write(buffer, 0, n);
        }
        return sw.toString();
    }

}
