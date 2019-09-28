package com.antigenomics.ilrfilter;

import java.util.List;

public interface KMerMatcherFactory<T, V extends KMerMatcher<T>> {
    V create(List<SequenceWithPayload<T>> reference);

    int getK();
}
