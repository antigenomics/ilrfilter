package com.antigenomics.ilrfilter;

import java.util.List;

public interface ReferenceProvider<T> {
    List<SequenceWithPayload<T>> getReferences(String species);
}
