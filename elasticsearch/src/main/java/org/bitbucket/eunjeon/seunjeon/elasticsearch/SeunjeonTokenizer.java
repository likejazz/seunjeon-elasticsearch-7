package org.bitbucket.eunjeon.seunjeon.elasticsearch;

import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.*;
import org.apache.lucene.analysis.util.RollingCharBuffer;
import org.apache.lucene.util.AttributeFactory;
import org.elasticsearch.common.logging.ESLoggerFactory;

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
    private TokenizerHelper tokenizerHelper;
    private final RollingCharBuffer buffer = new RollingCharBuffer();
    private int finalOffset;

    Logger logger = null;

    public SeunjeonTokenizer(TokenizerOptions options) {
        super(AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY);
        logger = ESLoggerFactory.getLogger(options.getName());

        initAttribute();
        tokenizerHelper = new TokenizerHelper(
                options.getDeCompound(),
                options.getDeInflect(),
                options.getIndexEojeol(),
                options.getPosTagging(),
                TokenizerHelper.convertPos(options.getIndexPoses()));

        tokenizerHelper.setMaxUnkLength(options.getMaxUnkLength());

        if (options.getUserDictPath() != null) {
            tokenizerHelper.setUserDict(options.getUserDictPath());
            logger.info(options.getUserDictPath() + " loading was successful.");
            if (options.getUserWords().size() > 0) {
                logger.warn("ignored \"user_words\". because settings of \"user_dict_path\"");
            }
        } else {
            tokenizerHelper.setUserDict(options.getUserWords());
        }
    }

    private void initAttribute() {
        charTermAtt = addAttribute(CharTermAttribute.class);
        posIncrAtt = addAttribute(PositionIncrementAttribute.class);
        posLenAtt = addAttribute(PositionLengthAttribute.class);
        offsetAtt = addAttribute(OffsetAttribute.class);
        typeAtt = addAttribute(TypeAttribute.class);
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (tokensQueue.isEmpty()) {
            return false;
        } else {
            LuceneToken pos = tokensQueue.poll();
            posIncrAtt.setPositionIncrement(pos.positionIncr());
            posLenAtt.setPositionLength(pos.positionLength());
            finalOffset = correctOffset(pos.endOffset());
            offsetAtt.setOffset(correctOffset(pos.beginOffset()), finalOffset );
            String term = pos.charTerm();
            charTermAtt.copyBuffer(term.toCharArray(), 0, term.length());
            typeAtt.setType(pos.poses());

            return true;
        }
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        tokensQueue = new LinkedList(tokenizerHelper.tokenize(getDocument()));
    }

    @Override
    public final void end() throws IOException {
        super.end();
        // set final offset
        offsetAtt.setOffset(finalOffset, finalOffset);
    }

    private String getDocument() throws IOException {
        StringWriter sw = new StringWriter();
        char[] buffer = new char[1024 * 4];
        int n;
        while (-1 != (n = input.read(buffer))) {
            sw.write(buffer, 0, n);
        }
        String docString = sw.toString().toLowerCase();
        return docString;
    }

}
