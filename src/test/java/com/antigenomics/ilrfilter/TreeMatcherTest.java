package com.antigenomics.ilrfilter;

import com.milaboratory.core.sequence.NucleotideSequence;
import com.milaboratory.core.tree.TreeSearchParameters;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class TreeMatcherTest {

    @Test
    public void getKmers() {
        TreeMatcher tm = new TreeMatcher<>(Arrays.asList(
                new SequenceWithPayloadImpl<>(new NucleotideSequence("CATGTC"), "1"),
                new SequenceWithPayloadImpl<>(new NucleotideSequence("AAGCAT"), "2")),
                5, new TreeSearchParameters(0, 0, 0)
        );

        assertEquals(8, tm.getKmers().size());
    }

    @Test
    public void match() {
        TreeMatcher tm = new TreeMatcher<>(Arrays.asList(
                new SequenceWithPayloadImpl<>(new NucleotideSequence("CATATGTCGATTC"), "1"),
                new SequenceWithPayloadImpl<>(new NucleotideSequence("ATAAACTGAGCAT"), "2")),
                6, new TreeSearchParameters(1, 0, 0)
        );

        assertEquals(Arrays.asList("1"), tm.match(new NucleotideSequence("TGTCAA")));
        assertEquals(Collections.emptyList(), tm.match(new NucleotideSequence("AGTCAA")));
    }
}