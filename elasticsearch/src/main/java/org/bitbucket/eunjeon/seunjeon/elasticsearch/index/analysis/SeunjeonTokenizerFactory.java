package org.bitbucket.eunjeon.seunjeon.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Tokenizer;
import org.bitbucket.eunjeon.seunjeon.elasticsearch.SeunjeonTokenizer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;

import org.elasticsearch.index.analysis.AbstractTokenizerFactory;
import org.elasticsearch.index.settings.IndexSettingsService;

public class SeunjeonTokenizerFactory extends AbstractTokenizerFactory {

    @Inject
    public SeunjeonTokenizerFactory(Index index,
                                    IndexSettingsService indexSettingsService,
                                    @Assisted String name,
                                    @Assisted Settings settings) {
        super(index, indexSettingsService.getSettings(), name, settings);
    }

    @Override
    public Tokenizer create() {
        return new SeunjeonTokenizer();
    }
}
