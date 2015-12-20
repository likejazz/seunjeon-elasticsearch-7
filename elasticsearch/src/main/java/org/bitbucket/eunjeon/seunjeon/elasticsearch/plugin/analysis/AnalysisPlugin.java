package org.bitbucket.eunjeon.seunjeon.elasticsearch.plugin.analysis;

import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.plugins.Plugin;

public class AnalysisPlugin extends Plugin {
    @Override
    public String name() {
        return "seunjeon-elasticsearch";
    }

    @Override
    public String description() {
        return "seunjeon analysis support. https://bitbucket.org/eunjeon/seunjeon";
    }

    public void onModule(AnalysisModule module) {
//        module.addTokenizer(
//                "mecab_ko_standard_tokenizer", MeCabKoStandardTokenizerFactory.class);
    }
}
