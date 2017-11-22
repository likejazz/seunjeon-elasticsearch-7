package org.bitbucket.eunjeon.seunjeon.elasticsearch;
/*

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.*;
import org.bitbucket.eunjeon.seunjeon.elasticsearch.index.analysis.SeunjeonTokenizerFactory;
import org.bitbucket.eunjeon.seunjeon.elasticsearch.plugin.analysis.AnalysisSeunjeonPlugin;
import org.elasticsearch.Version;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.test.ESTestCase;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.instanceOf;


public class SeunjeonAnalysisTest extends ESTestCase {

    public void testTemp() throws IOException {
        TestAnalysis analysis = createTestAnalysis();
        TokenizerFactory tokenizerFactory = analysis.tokenizer.get("seunjeon_tokenizer");
        assertThat(tokenizerFactory, instanceOf(SeunjeonTokenizerFactory.class));
        Tokenizer tokenizer = tokenizerFactory.create();
        assertEquals("삼성/N:1:1:0:2:N;삼성전자/EOJ:0:2:0:4:EOJ;전자/N:1:1:2:4:N;", tokenize("삼성전자", tokenizer));


//
//        IndexAnalyzers indexAnalyzers = analysis.indexAnalyzers;
//        NamedAnalyzer analyzer = indexAnalyzers.get("korean");

    }

    private static TestAnalysis createTestAnalysis() throws IOException {
//        InputStream empty_dict = KuromojiAnalysisTests.class.getResourceAsStream("empty_user_dict.txt");
//        InputStream dict = KuromojiAnalysisTests.class.getResourceAsStream("user_dict.txt");
        Path home = createTempDir();
        Path config = home.resolve("config");
        Files.createDirectory(config);
//        Files.copy(empty_dict, config.resolve("empty_user_dict.txt"));
//        Files.copy(dict, config.resolve("user_dict.txt"));
        String json = "/org/bitbucket/eunjeon/seunjeon/elasticsearch/seunjeon_analysis.json";

        Settings settings = Settings.builder()
                .loadFromStream(json, SeunjeonAnalysisTest.class.getResourceAsStream(json))
                .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
                .build();
        Settings nodeSettings = Settings.builder().put(Environment.PATH_HOME_SETTING.getKey(), home).build();
        return createTestAnalysis(new Index("test", "_na_"), nodeSettings, settings, new AnalysisSeunjeonPlugin());
    }

    public String tokenize(String document, Tokenizer tokenizer) throws IOException {
        tokenizer.setReader(new StringReader(document));
        tokenizer.reset();
        StringBuilder sb = new StringBuilder();
        while (tokenizer.incrementToken()) {
            CharTermAttribute termAttr = tokenizer.getAttribute(CharTermAttribute.class);
            PositionIncrementAttribute posIncrAttr = tokenizer.getAttribute(PositionIncrementAttribute.class);
            PositionLengthAttribute posLength = tokenizer.getAttribute(PositionLengthAttribute.class);
            OffsetAttribute offsetAttr = tokenizer.getAttribute(OffsetAttribute.class);
            TypeAttribute typeAttr = tokenizer.getAttribute(TypeAttribute.class);
            String toString = new String(termAttr.buffer(), 0, termAttr.length()) + ":" +
                    posIncrAttr.getPositionIncrement() + ":" +
                    posLength.getPositionLength() + ":" +
                    offsetAttr.startOffset() + ":" +
                    offsetAttr.endOffset() + ":" +
                    typeAttr.type();
            sb.append(toString);
            sb.append(";");
        }
        tokenizer.close();
        return sb.toString();

    }
}
*/
