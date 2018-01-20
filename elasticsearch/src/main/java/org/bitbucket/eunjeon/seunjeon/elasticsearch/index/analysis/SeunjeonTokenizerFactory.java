package org.bitbucket.eunjeon.seunjeon.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Tokenizer;
import org.bitbucket.eunjeon.seunjeon.elasticsearch.SeunjeonTokenizer;
import org.bitbucket.eunjeon.seunjeon.elasticsearch.TokenizerOptions;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;

import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;


public class SeunjeonTokenizerFactory extends AbstractTokenizerFactory {
    private TokenizerOptions options;

    public SeunjeonTokenizerFactory(IndexSettings indexSettings,
                                    Environment env,
                                    String name,
                                    Settings settings) {
        super(indexSettings, name, settings);

        this.options = TokenizerOptions.create(name).
                setCompress(settings.getAsBoolean("compress", TokenizerOptions.COMPRESS)).
                setUserDictPath(getFullPath(env, settings.get("user_dict_path", null))).
                setUserWords(settings.getAsList("user_words", Collections.emptyList())).
                setDeCompound(settings.getAsBoolean("decompound", TokenizerOptions.DECOMPOUND)).
                setDeInflect(settings.getAsBoolean("deinflect", TokenizerOptions.DEINFLECT)).
                setIndexEojeol(settings.getAsBoolean("index_eojeol", TokenizerOptions.INDEX_EOJEOL)).
                setIndexPoses(settings.getAsList("index_poses", TokenizerOptions.INDEX_POSES)).
                setPosTagging(settings.getAsBoolean("pos_tagging", TokenizerOptions.POS_TAGGING)).
                setMaxUnkLength(settings.getAsInt("max_unk_length", TokenizerOptions.MAX_UNK_LENGTH));
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
