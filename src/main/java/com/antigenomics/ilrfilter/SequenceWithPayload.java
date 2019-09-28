package com.antigenomics.ilrfilter;

import com.milaboratory.core.sequence.NucleotideSequence;

public interface SequenceWithPayload<T> {
    T getPayload();
    NucleotideSequence getSequence();
}
