package org.bitbucket.eunjeon.seunjeon;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class CompressionHelperTest {
//
//
//    @Test
//    public void testStrCompressionAndUncompression() {
//        String testString = "Sample test";
//        byte[] compressedStr = CompressionHelper.compressStr(testString);
//        // this is ensure memory address of the compressed str is same i.e test string canonicalization
//        Assert.assertTrue(CompressionHelper.compressStr(testString) == compressedStr);
//        Assert.assertEquals(CompressionHelper.uncompressStr(compressedStr), testString);
//
//    }
//
//    @Test
//    public void testObjectCompressionAndUncompressFromFile() throws Exception {
//        String fileLocation = File.createTempFile("comp_test", ".temp").getPath();
//        FileOutputStream fos = new FileOutputStream(fileLocation);
//        String sample = "great";
//        CompressionHelper.compressObjectAndSave(sample, fos);
//        fos.close();
//
//        FileInputStream fis = new FileInputStream(fileLocation);
//
//        String uncompressStr = CompressionHelper.uncompressAndReadObject(fis);
//        Assert.assertEquals(sample, uncompressStr);
//    }
}
