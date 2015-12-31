package org.bitbucket.eunjeon.seunjeon.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Tokenizer;
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
        TokenizerOptions options = new TokenizerOptions();
        options.userWords = settings.getAsArray("user_words");
        options.deCompound = settings.getAsBoolean("decompound", true);
        options.deInflect = settings.getAsBoolean("deinflect", true);
        options.indexEojeol = settings.getAsBoolean("index_eojeol", true);
        options.indexPoses = settings.getAsArray("index_poses", new String[]{
                "N",    // 체언
                "SL",   // 외국어
                "SH",   // 한자
                "SN",   // 숫자
                "XR",   // 어근
                "V",    // 용언
                "UNK"   // 미지어
        });
        this.options = options;
    }

    @Override
    public Tokenizer create() {
        return new SeunjeonTokenizer(options);
    }
}
