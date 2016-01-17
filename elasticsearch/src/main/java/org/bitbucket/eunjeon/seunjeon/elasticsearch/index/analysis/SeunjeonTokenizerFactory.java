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
import org.elasticsearch.index.analysis.Analysis;
import org.elasticsearch.index.settings.IndexDynamicSettings;
import org.elasticsearch.index.settings.IndexSettingsService;


import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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

        // TODO: 미리 로딩하면 사전을 두벌 가지게 되는거 아닌가? seunjeon에서 직접 로딩해야할듯
        List<String> userDict = getUserDict(env, settings, "user_dict_path", "user_words");
        this.options = TokenizerOptions.create().
                setUserDict(userDict).
                setDeCompound(settings.getAsBoolean("decompound", TokenizerOptions.DECOMPOUND)).
                setDeInflect(settings.getAsBoolean("deinflect", TokenizerOptions.DEINFLECT)).
                setIndexEojeol(settings.getAsBoolean("index_eojeol", TokenizerOptions.INDEX_EOJEOL)).
                setIndexPoses(settings.getAsArray("index_poses", TokenizerOptions.INDEX_POSES));
    }

    private List<String> getUserDict(Environment env,
                                     Settings settings,
                                     String userDictPathName,
                                     String userWordsName) {
        List<String> userDict = new LinkedList<>();
        String[] userWords = settings.getAsArray(userWordsName, new String[0]);
        logger.info(userWordsName + " loaded. count = " + userWords.length);
        userDict.addAll(Arrays.asList(userWords ));

        Reader userDictReader = Analysis.getReaderFromFile(env, settings, userDictPathName);
        if (userDictReader == null) {
            return userDict;
        }

        try {
            List<String> userDictFromFile = Analysis.loadWordList(userDictReader, "#");
            logger.info(userDictPathName + " loaded. count = " + userDictFromFile.size());
            userDict.addAll(userDictFromFile);
        } catch (IOException e) {
            throw new ElasticsearchException("failed to load seunjeon user dictionary", e);
        }
        return userDict;
    }

    @Override
    public Tokenizer create() {
        return new SeunjeonTokenizer(options);
    }
}
