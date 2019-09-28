package com.antigenomics.ilrfilter;

import com.milaboratory.core.sequence.NucleotideSequence;

public final class SequenceWithPayloadImpl<T> implements SequenceWithPayload<T> {
    private final T payload;
    private final NucleotideSequence sequence;

    public SequenceWithPayloadImpl(NucleotideSequence sequence, T payload) {
        this.sequence = sequence;
        this.payload = payload;
    }

    public SequenceWithPayloadImpl(String sequence, T payload) {
        this.sequence = new NucleotideSequence(sequence);
        this.payload = payload;
    }

    @Override
    public T getPayload() {
        return payload;
    }

    @Override
    public NucleotideSequence getSequence() {
        return sequence;
    }
}
