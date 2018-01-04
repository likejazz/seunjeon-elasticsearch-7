package org.bitbucket.eunjeon.seunjeon;


import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Collection of methods for helping compression
 */
public class CompressionHelper {

    private static Logger logger = LoggerFactory.getLogger(CompressionHelper.class);

    private static final Charset UTF_8 = Charset.forName("UTF8");

    private static Map<String, byte[]> stringCache = new ConcurrentHashMap<String, byte[]>(1000);

    public static byte[] compress(byte[] inputBytes) {

        final byte[][] utf8String = new byte[1][1];
        AccessController.doPrivileged(
                (PrivilegedAction<Void>) () -> {
                    try {
                        utf8String[0] = Snappy.compress(inputBytes);
                    } catch (IOException e) {
                        logger.error("IOException thrown when compressing ", e);
                    }
                    return null;
                }
        );
        return utf8String[0];

    }

    public static byte[] uncompress(byte[] str) {

        final byte[][] uncompressedBytes = new byte[1][1];
        AccessController.doPrivileged(
                (PrivilegedAction<Void>) () -> {
                    try {
                        uncompressedBytes[0] = Snappy.uncompress(str);
                    } catch (IOException e) {
                        logger.error("IOException thrown when uncompressing", e);
                    }
                    return null;
                }
        );

        return uncompressedBytes[0];

    }

    /**
     * Compresses the given string provided.
     * It compresses the string by doing the following
     * 1. Converts/Strips the string to UTF-8
     * 2. Caches the string, so any future request to same string will give same compressed result reference
     * 3. Converts the resultant UTF-8 string into bytes
     * 4. Compresses the bytes using snappy compression algo
     * and returns the compressed byte array
     * @param str
     * @return
     */
    public static byte[] compressStr(String str) {
        if (stringCache.containsKey(str)) {
            return stringCache.get(str);
        }
        final byte[] compressedStringBytes = compress(str.getBytes(UTF_8));
        if (stringCache.size() >= 10000) {
            stringCache.clear();
        }
        stringCache.putIfAbsent(str, compressedStringBytes);
        return compressedStringBytes;

    }

    /**
     * Uncompress the compressed byte array provided to respective string
     * @param str
     * @return
     */
    public static String uncompressStr(byte[] str) {
        final String[] utf8String = new String[1];
        utf8String[0] = new String(uncompress(str), UTF_8);
        return utf8String[0];
    }

    /**
     * Serializes given object, then perform GZIP Compression of the object and
     * writes to given outputstream
     * @param object
     * @param os
     */
    public static void compressObjectAndSave(Object object, OutputStream os) {
        final byte[][] compressedObject = {null};
        AccessController.doPrivileged(
                (PrivilegedAction<Void>) () -> {

                    try {
                        LZ4BlockOutputStream zipOut = new LZ4BlockOutputStream(os);
                        ObjectOutputStream objectOut = new ObjectOutputStream(zipOut);
                        long startTime = System.nanoTime();
                        objectOut.writeObject(object);
                        logger.info("Time taken to perform GZIP compression " +
                                ((System.nanoTime() - startTime) / (1000 * 1000)) + "ms");
                        objectOut.close();
                    } catch (IOException e) {
                        logger.error("Unable to compress object ", e);
                    }

                    return null;
                }
        );

    }

    /**
     * Uncompresses the given input stream (using GZIP decompression) and de-serializes to required Object of type T
     * @param is
     * @param <T>
     * @return
     */
    public static <T> T uncompressAndReadObject(InputStream is) {
        final T[] myObj1 = (T[]) new Object[]{null};
        AccessController.doPrivileged(
                (PrivilegedAction<Void>) () -> {
                    try {
                        logger.info("Starting GZIp Decompressing ");
                        LZ4BlockInputStream zipIn = new LZ4BlockInputStream(is);
                        ObjectInputStream objectIn = new ObjectInputStream(zipIn);
                        long startTime = System.nanoTime();
                        myObj1[0] = (T) objectIn.readObject();
                        logger.info("Time taken for GZIP Decompression is " +
                                ((System.nanoTime() - startTime) / (1000 * 1000)) + "ms");
                        objectIn.close();
                    } catch (IOException e) {
                        logger.error("Unable to un-compress object ", e);
                    } catch (ClassNotFoundException e) {
                        logger.error("Unable to find given class ", e);
                    }
                    return null;
                }
        );

        return myObj1[0];
    }
    
}
