package org.bitbucket.eunjeon.seunjeon.elasticsearch;

import org.apache.lucene.analysis.Analyzer;


public class SeunjeonAnalyzer extends Analyzer {
    public SeunjeonAnalyzer() { }

    @Override
    protected Analyzer.TokenStreamComponents createComponents(final String fieldName) {
        return new Analyzer.TokenStreamComponents(new SeunjeonTokenizer(TokenizerOptions.create("seunjeon_analyzer") ));
    }

}
