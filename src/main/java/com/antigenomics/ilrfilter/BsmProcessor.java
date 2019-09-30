package com.antigenomics.ilrfilter;

import cc.redberry.pipe.Processor;
import com.milaboratory.core.io.sequence.SequenceRead;

@FunctionalInterface
public interface BsmProcessor<R extends SequenceRead, T> extends Processor<R, AnnotatedRead<R,T>> {
}
