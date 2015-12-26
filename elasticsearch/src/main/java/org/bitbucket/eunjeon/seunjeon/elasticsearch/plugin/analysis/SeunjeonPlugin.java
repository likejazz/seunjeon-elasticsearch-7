package org.bitbucket.eunjeon.seunjeon.elasticsearch.plugin.analysis;

import org.bitbucket.eunjeon.seunjeon.elasticsearch.index.analysis.SeunjeonTokenizerFactory;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.plugins.Plugin;

public class SeunjeonPlugin  extends Plugin {
    @Override
    public String name() {
        return "seunjeon-elasticsearch";
    }

    @Override
    public String description() {
        return "mecab-ko-lucene-analyzer analysis support";
    }
    public void onModule(AnalysisModule module) {
        module.addTokenizer("seunjeon_tokenizer", SeunjeonTokenizerFactory.class);
    }
}
