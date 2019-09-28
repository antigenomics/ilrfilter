package com.antigenomics.ilrfilter;

import java.util.List;

public final class HashMatcherFactory<T> implements KMerMatcherFactory<T, HashMatcher<T>> {
    private final int k;
    private final int mismatches;

    public HashMatcherFactory(int k, int mismatches) {
        this.k = k;
        this.mismatches = mismatches;
    }

    @Override
    public HashMatcher<T> create(List<SequenceWithPayload<T>> reference) {
        return new HashMatcher<>(reference, k, mismatches);
    }

    @Override
    public int getK() {
        return k;
    }
}
