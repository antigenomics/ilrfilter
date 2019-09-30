package com.antigenomics.ilrfilter;

import com.milaboratory.core.io.sequence.SequenceRead;

import java.util.Collections;
import java.util.List;

public final class AnnotatedRead<R extends SequenceRead, T> {
    private final List<T> annotations;
    private final R read;

    public AnnotatedRead(List<T> annotations, R read) {
        this.annotations = annotations;
        this.read = read;
    }

    public List<T> getAnnotations() {
        return Collections.unmodifiableList(annotations);
    }

    public boolean isAnnotated() {
        return !annotations.isEmpty();
    }

    public R getRead() {
        return read;
    }
}
