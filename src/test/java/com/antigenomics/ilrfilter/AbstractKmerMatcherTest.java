package com.antigenomics.ilrfilter;

import com.milaboratory.core.sequence.NucleotideSequence;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class AbstractKmerMatcherTest {
    @Test
    public void matchOffset() {
        var shm = new HashMatcher<>(Arrays.asList(
                new SequenceWithPayloadImpl<>("ATGCAC", "1")),
                6, 1);
        assertEquals(Collections.emptyList(), shm.matchWithOffset(new NucleotideSequence("AATGCAC"), 0, true));
        assertEquals(List.of("1"), shm.matchWithOffset(new NucleotideSequence("AATGCAC"), 1, true));
    }
}