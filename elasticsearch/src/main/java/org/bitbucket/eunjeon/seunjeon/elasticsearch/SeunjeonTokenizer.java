package org.bitbucket.eunjeon.seunjeon.elasticsearch;

import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.*;
import org.apache.lucene.util.AttributeFactory;
import org.elasticsearch.common.logging.ESLoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;


public class SeunjeonTokenizer extends Tokenizer {
    private CharTermAttribute charTermAtt;
    private PositionIncrementAttribute posIncrAtt;
    private PositionLengthAttribute posLenAtt;
    private OffsetAttribute offsetAtt;
    private TypeAttribute typeAtt;
    private Queue<LuceneToken> tokensQueue;
    private TokenBuilder tokenBuilder;
    Logger logger = null;

    public SeunjeonTokenizer(TokenizerOptions options) {
        super(AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY);
        logger = ESLoggerFactory.getLogger(options.getName());

        initAttribute();
        TokenBuilder.setMaxUnkLength(options.getMaxUnkLength());
        if (options.getUserDictPath() != null) {
            TokenBuilder.setUserDict(options.getUserDictPath());
            logger.info(options.getUserDictPath() + " loading was successful.");
            if (options.getUserWords().length > 0) {
                logger.warn("ignored \"user_words\". because settings of \"user_dict_path\"");
            }
        } else {
            TokenBuilder.setUserDict(Arrays.asList(options.getUserWords()).iterator());
        }
        tokenBuilder = new TokenBuilder(
                options.getDeCompound(),
                options.getDeInflect(),
                options.getIndexEojeol(),
                options.getPosTagging(),
                TokenBuilder.convertPos(options.getIndexPoses()));
    }

    private void initAttribute() {
        charTermAtt = addAttribute(CharTermAttribute.class);
        posIncrAtt = addAttribute(PositionIncrementAttribute.class);
        posLenAtt = addAttribute(PositionLengthAttribute.class);
        offsetAtt = addAttribute(OffsetAttribute.class);
        typeAtt = addAttribute(TypeAttribute.class);
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        tokensQueue = new LinkedList(tokenBuilder.tokenize(getDocument()));
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
            String term = pos.charTerm();
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
        return sw.toString().toLowerCase();
    }

}
