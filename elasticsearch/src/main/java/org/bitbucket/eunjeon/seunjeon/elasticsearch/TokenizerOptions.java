package org.bitbucket.eunjeon.seunjeon.elasticsearch;

public class TokenizerOptions {
    public final static boolean DECOMPOUND = true;
    public final static boolean DEINFLECT = true;
    public final static boolean INDEX_EOJEOL = true;
    public final static String[] INDEX_POSES = TokenBuilder.INDEX_POSES_JAVA();

    private String[] userWords = new String[0];
    private boolean deCompound = DECOMPOUND;
    private boolean deInflect = DEINFLECT;
    private boolean indexEojeol = INDEX_EOJEOL;
    private String[] indexPoses = INDEX_POSES;

    public static TokenizerOptions create() {
        return new TokenizerOptions();
    }

    public TokenizerOptions setUserWords(String[] userWords) {
        this.userWords = userWords;
        return this;
    }

    public TokenizerOptions setDeCompound(boolean deCompound) {
        this.deCompound = deCompound;
        return this;
    }

    public TokenizerOptions setDeInflect(boolean deInflect) {
        this.deInflect = deInflect;
        return this;
    }

    public TokenizerOptions setIndexEojeol(boolean indexEojeol) {
        this.indexEojeol = indexEojeol;
        return this;
    }

    public TokenizerOptions setIndexPoses(String[] indexPoses) {
        this.indexPoses = indexPoses;
        return this;
    }

    public String[] getUserWords() {
        return this.userWords;
    }

    public boolean getDeCompound() {
        return this.deCompound;
    }

    public boolean getDeInflect() {
        return this.deInflect;
    }

    public boolean getIndexEojeol() {
        return this.indexEojeol;
    }

    public String[] getIndexPoses() {
        return this.indexPoses;
    }
}
