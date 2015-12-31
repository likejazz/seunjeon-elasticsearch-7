package org.bitbucket.eunjeon.seunjeon.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.queryparser.classic.Token;
import org.bitbucket.eunjeon.seunjeon.elasticsearch.SeunjeonTokenizer;
import org.bitbucket.eunjeon.seunjeon.elasticsearch.TokenizerOptions;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;

import org.elasticsearch.index.analysis.AbstractTokenizerFactory;
import org.elasticsearch.index.settings.IndexSettingsService;

public class SeunjeonTokenizerFactory extends AbstractTokenizerFactory {
    private TokenizerOptions options;

    @Inject
    public SeunjeonTokenizerFactory(Index index,
                                    IndexSettingsService indexSettingsService,
                                    @Assisted String name,
                                    @Assisted Settings settings) {
        super(index, indexSettingsService.getSettings(), name, settings);
        this.options = TokenizerOptions.create().
                setUserWords(settings.getAsArray("user_words")).
                setDeCompound(settings.getAsBoolean("decompound", TokenizerOptions.DECOMPOUND)).
                setDeInflect(settings.getAsBoolean("deinflect", TokenizerOptions.DEINFLECT)).
                setIndexEojeol(settings.getAsBoolean("index_eojeol", TokenizerOptions.INDEX_EOJEOL)).
                setIndexPoses(settings.getAsArray("index_poses", TokenizerOptions.INDEX_POSES));
    }

    @Override
    public Tokenizer create() {
        return new SeunjeonTokenizer(options);
    }
}
