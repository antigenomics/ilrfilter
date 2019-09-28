package com.antigenomics.ilrfilter;

import com.milaboratory.core.io.sequence.PairedRead;
import com.milaboratory.core.io.sequence.SingleRead;
import com.milaboratory.core.sequence.NucleotideSequence;

import java.util.Collections;
import java.util.List;

public final class BoundarySegmentMatcher<T, V extends AbstractKmerMatcher<T>> {
    private final AbstractKmerMatcher<T> matcher;
    private final int maxOffset;
    private final boolean outerBoundMatchOnly;

    public BoundarySegmentMatcher(KMerMatcherFactory<T, V> matcherFactory,
                                  ReferenceProvider<T> referenceProvider,
                                  String species,
                                  int maxOffset,
                                  boolean outerBoundMatchOnly) {
        this.matcher = matcherFactory.create(referenceProvider.getReferences(species));
        this.maxOffset = maxOffset;
        this.outerBoundMatchOnly = outerBoundMatchOnly;
    }

    public List<T> match(SingleRead read, boolean first) {
        NucleotideSequence nucleotideSequence = read.getData().getSequence();
        int currentMaxOffset = Math.min(nucleotideSequence.size() - matcher.getK(), maxOffset);
        if (currentMaxOffset < 0) {
            return Collections.emptyList();
        }
        List<T> result = matcher.matchWithOffset(nucleotideSequence, currentMaxOffset, first);
        if (!outerBoundMatchOnly) {
            result.addAll(matcher.matchWithOffset(nucleotideSequence, currentMaxOffset, !first));
        }
        return result;
    }

    public List<T> match(PairedRead pairedRead) {
        List<T> result = match(pairedRead.getR1(), true);
        result.addAll(match(pairedRead.getR2(), false));
        return result;
    }

    public KMerMatcher<T> getMatcher() {
        return matcher;
    }
}
