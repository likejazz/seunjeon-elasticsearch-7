package org.bitbucket.eunjeon.seunjeon.elasticsearch.plugin.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.bitbucket.eunjeon.seunjeon.elasticsearch.index.analysis.SeunjeonAnalyzerProvider;
import org.bitbucket.eunjeon.seunjeon.elasticsearch.index.analysis.SeunjeonTokenizerFactory;
import org.elasticsearch.index.analysis.AnalyzerProvider;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.indices.analysis.AnalysisModule;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;

import java.util.Map;

import static java.util.Collections.singletonMap;

public class AnalysisSeunjeonPlugin extends Plugin implements AnalysisPlugin {

    @Override
    public Map<String, AnalysisModule.AnalysisProvider<TokenizerFactory>> getTokenizers() {
        return singletonMap("seunjeon_tokenizer", SeunjeonTokenizerFactory::new);
    }

    @Override
    public Map<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> getAnalyzers() {
        return singletonMap("seunjeon_analyzer", SeunjeonAnalyzerProvider::new);
    }

}
