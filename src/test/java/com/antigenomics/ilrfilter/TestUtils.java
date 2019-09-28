package com.antigenomics.ilrfilter;

import com.milaboratory.core.sequence.NucleotideSequence;
import com.milaboratory.core.sequence.SequenceBuilder;

import java.io.InputStream;
import java.util.Random;

public final class TestUtils {
    private static final Random random = new Random(480011L);
    private static final TestUtils THIS = new TestUtils();

    private TestUtils(){

    }

    public static InputStream getResourceAsStream(String path) {
        return THIS.getClass().getClassLoader().getResourceAsStream(path);
    }

    public static int nextFromRange(int min, int max) {
        return max > min ? min + random.nextInt(max - min + 1) : max;
    }

    public static int nextIndex(int n) {
        return random.nextInt(n);
    }

    public static NucleotideSequence randomSequence(int length) {
        SequenceBuilder<NucleotideSequence> builder = NucleotideSequence.ALPHABET.createBuilder();
        for (int i = 0; i < length; ++i) {
            builder.append((byte) random.nextInt(4));
        }
        return builder.createAndDestroy();
    }

    public static NucleotideSequence randomSequence(int lengthMin, int lengthMax) {
        int length = nextFromRange(lengthMin, lengthMax);
        return randomSequence(length);
    }
}
