package org.bitbucket.eunjeon.seunjeon.elasticsearch;

public class TokenizerOptions {
    public String[] userWords = new String[0];
    public boolean deCompound = true;
    public boolean deInflect = true;
    public boolean indexEojeol = true;
    public String[] indexPoses = TokenBuilder.INDEX_POSES_JAVA();
}
