package org.bitbucket.eunjeon.seunjeon.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Tokenizer;
import org.bitbucket.eunjeon.seunjeon.elasticsearch.SeunjeonTokenizer;
import org.bitbucket.eunjeon.seunjeon.elasticsearch.TokenizerOptions;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;

import org.elasticsearch.index.analysis.AbstractTokenizerFactory;
import org.elasticsearch.index.settings.IndexSettingsService;


import java.nio.file.Path;
import org.elasticsearch.common.logging.Loggers;


public class SeunjeonTokenizerFactory extends AbstractTokenizerFactory {
    private TokenizerOptions options;
    ESLogger logger = null;

    @Inject
    public SeunjeonTokenizerFactory(Index index,
                                    IndexSettingsService indexSettingsService,
                                    Environment env,
                                    @Assisted String name,
                                    @Assisted Settings settings) {
        super(index, indexSettingsService.getSettings(), name, settings);
        logger = Loggers.getLogger(name);

        this.options = TokenizerOptions.create(name).
                setUserDictPath(getFullPath(env, settings.get("user_dict_path", null))).
                setUserWords(settings.getAsArray("user_words", new String[0])).
                setDeCompound(settings.getAsBoolean("decompound", TokenizerOptions.DECOMPOUND)).
                setDeInflect(settings.getAsBoolean("deinflect", TokenizerOptions.DEINFLECT)).
                setIndexEojeol(settings.getAsBoolean("index_eojeol", TokenizerOptions.INDEX_EOJEOL)).
                setIndexPoses(settings.getAsArray("index_poses", TokenizerOptions.INDEX_POSES)).
                setPosTagging(settings.getAsBoolean("pos_tagging", TokenizerOptions.POS_TAGGING));
    }

    private String getFullPath(Environment env, String path) {
        String result;
        if (path == null) {
            return null;
        }
        final Path fullPath = env.configFile().resolve(path);

        result = fullPath.toString();
        return result;
    }

    @Override
    public Tokenizer create() {
        return new SeunjeonTokenizer(options);
    }
}
