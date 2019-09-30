package com.antigenomics.ilrfilter;

import com.milaboratory.core.sequence.NucleotideSequence;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class HashMatcherTest {
    @Test
    public void match() {
        HashMatcher shm = new HashMatcher<>(Arrays.asList(
                new SequenceWithPayloadImpl<>("AACGAA", "1"),
                new SequenceWithPayloadImpl<>("AAAAAA", "2")),
                6, 1);
        System.out.println(shm.getKmers().size());
        Assert.assertEquals(Arrays.asList("2"), shm.match(new NucleotideSequence("AAATAA")));
        Assert.assertEquals(Arrays.asList("1", "2"), shm.match(new NucleotideSequence("AAAGAA")));
        Assert.assertEquals(Collections.emptyList(), shm.match(new NucleotideSequence("AACGTT")));
    }

    @Test
    public void getKmers() {
        HashMatcher shm = new HashMatcher<>(Collections.emptyList(), 3, 0);
        shm.put(new NucleotideSequence("TCG"), new Object(), 0);
        shm.put(new NucleotideSequence("CGA"), new Object(), 0);
        System.out.println(shm.getKmers());
        Assert.assertEquals(new HashSet<>(Arrays.asList(new NucleotideSequence("TCG"),
                new NucleotideSequence("CGA"))), shm.getKmers());

        HashMatcher shm2 = new HashMatcher<>(Collections.emptyList(), 3, 0);
        shm2.put(new NucleotideSequence("TCG"), new Object(), 1);
        System.out.println(shm2.getKmers());
        Assert.assertEquals(3 * 3 + 1, shm2.getKmers().size());

        HashMatcher shm3 = new HashMatcher<>(Collections.emptyList(), 3, 0);
        shm3.put(new NucleotideSequence("TCG"), new Object(), 2);
        System.out.println(shm2.getKmers());
        Assert.assertEquals(3 * 3 + 1, shm2.getKmers().size());
    }

    @Test
    public void getKmer() {
        HashMatcher shm = new HashMatcher<>(Collections.emptyList(), 4, 0);

        long kmer = shm.getKmer(new NucleotideSequence("ACGTACGTAGC"));
        print(kmer);
        Assert.assertEquals(39L, kmer);
    }

    @Test
    public void getSequence() {
        HashMatcher shm = new HashMatcher<>(Collections.emptyList(), 4, 0);

        Assert.assertEquals(new NucleotideSequence("ACGT"), shm.getSequence(39L));
    }

    private void print(long x) {
        System.out.println(String.format("%064d", new BigInteger(Long.toBinaryString(x))));
    }
}