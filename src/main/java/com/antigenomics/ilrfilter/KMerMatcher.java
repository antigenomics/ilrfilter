package com.antigenomics.ilrfilter;

import com.milaboratory.core.sequence.NucleotideSequence;

import java.util.List;
import java.util.Set;

public interface KMerMatcher<T> {
    List<T> match(NucleotideSequence query);

    Set<NucleotideSequence> getKmers();

    int getK();
}
