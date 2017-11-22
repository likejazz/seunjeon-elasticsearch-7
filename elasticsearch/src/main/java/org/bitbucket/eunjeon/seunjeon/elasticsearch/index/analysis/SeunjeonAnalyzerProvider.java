package org.bitbucket.eunjeon.seunjeon.elasticsearch.index.analysis;

import org.bitbucket.eunjeon.seunjeon.elasticsearch.SeunjeonAnalyzer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;


public class SeunjeonAnalyzerProvider extends AbstractIndexAnalyzerProvider<SeunjeonAnalyzer> {
    private final SeunjeonAnalyzer analyzer;

    public SeunjeonAnalyzerProvider(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(indexSettings, name, settings);
        analyzer = new SeunjeonAnalyzer();
    }

    @Override
    public SeunjeonAnalyzer get() {
        return analyzer;
    }

}
