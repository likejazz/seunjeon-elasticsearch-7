package org.bitbucket.eunjeon.seunjeon.elasticsearch.plugin.analysis;

import org.bitbucket.eunjeon.seunjeon.elasticsearch.index.analysis.SeunjeonTokenizerFactory;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.plugins.Plugin;

public class AnalysisSeunjeonPlugin extends Plugin {
    @Override
    public String name() {
        return "analysis-seunjeon";
    }

    @Override
    public String description() {
        return "seunjeon analysis support. https://bitbucket.org/eunjeon/seunjeon";
    }

    public void onModule(AnalysisModule module) {
        module.addTokenizer("seunjeon_tokenizer", SeunjeonTokenizerFactory.class);
    }
}
