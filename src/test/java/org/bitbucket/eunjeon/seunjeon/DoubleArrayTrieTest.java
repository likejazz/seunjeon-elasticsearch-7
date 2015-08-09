package org.bitbucket.eunjeon.seunjeon;


import org.bitbucket.eunjeon.seunjeon.DoubleArrayTrie;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * Created by parallels on 7/26/15.
 */
public class DoubleArrayTrieTest {
    @Test
    public void testSearch() {
        List<String> keywords = new ArrayList<String>();
        keywords.add("가가");
        keywords.add("감자");
        keywords.add("고구마");
        keywords.add("고구마");
        keywords.add("양파");

        int [] values = {1, 2, 3, 4, 5};

        DoubleArrayTrie da = new DoubleArrayTrie();
//        da.build(keywords);
        da.build(keywords, null, values, keywords.size());
        assertEquals(3, da.exactMatchSearch("고구마"));
        assertEquals("[3]", da.commonPrefixSearch("고구마").toString());
        assertEquals("[3]", da.commonPrefixSearch("감고구마", 1, 4, 0).toString());
    }
}
