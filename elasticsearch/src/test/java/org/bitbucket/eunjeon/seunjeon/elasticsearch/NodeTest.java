package org.bitbucket.eunjeon.seunjeon.elasticsearch;

/*
import org.bitbucket.eunjeon.seunjeon.elasticsearch.plugin.analysis.AnalysisSeunjeonPlugin;
import org.elasticsearch.Version;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.compress.CompressedXContent;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexService;
import org.elasticsearch.index.mapper.DocumentMapper;
import org.elasticsearch.index.mapper.DocumentMapperParser;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.elasticsearch.test.InternalSettingsPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;


public class NodeTest extends ESSingleNodeTestCase {
    @Override
    protected Collection<Class<? extends Plugin>> getPlugins() {
        return pluginList(InternalSettingsPlugin.class, AnalysisSeunjeonPlugin.class);
    }

    protected Settings nodeSettings() {
        Path home = createTempDir();
        Path config = home.resolve("config");
        try {
            Files.createDirectory(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Settings.builder().put(Environment.PATH_HOME_SETTING.getKey(), home).build();
    }

    public void testIndex() throws IOException {
        String json = "/org/bitbucket/eunjeon/seunjeon/elasticsearch/seunjeon_analysis.json";

        Settings settings = Settings.builder()
                .loadFromStream(json, NodeTest.class.getResourceAsStream(json))
                .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
                .build();

        IndexService indexService = createIndex("seunjeon-idx", settings);
        DocumentMapperParser mapperParser = indexService.mapperService().documentMapperParser();
//        DocumentMapper mapper = mapperParser.parse("type", new CompressedXContent(mapping.string()));

        String mapping = XContentFactory.jsonBuilder().startObject().startObject("type").startObject("properties")
                .startObject("foo").field("enabled", false).endObject()
                .startObject("bar").field("type", "integer").endObject()
                .endObject().endObject().endObject().string();

        System.out.println(mapping);



    }
}
*/
