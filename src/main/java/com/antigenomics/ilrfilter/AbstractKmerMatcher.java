package com.antigenomics.ilrfilter;

import com.milaboratory.core.sequence.NucleotideSequence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractKmerMatcher<T> implements KMerMatcher<T> {
    protected final int k;

    public AbstractKmerMatcher(int k) {
        this.k = k;
    }

    protected abstract List<T> matchInner(NucleotideSequence query);

    @Override
    public List<T> match(NucleotideSequence query) {
        if (query.size() != k) {
            throw new IllegalArgumentException("Sequence should have length of k=" + k);
        }

        List<T> res = matchInner(query);

        return res == null ? Collections.emptyList() : res;
    }

    public List<T> matchWithOffset(NucleotideSequence query, int maxOffset, boolean fromLeft) {
        if (query.size() < k + maxOffset) {
            throw new IllegalArgumentException("Query should be longer than k+offset=" + k + maxOffset);
        }

        List<T> results = new ArrayList<>();
        if (fromLeft) {
            for (int i = 0; i <= maxOffset; i++) {
                results.addAll(match(query.getRange(i, i + k)));
            }
        } else {
            for (int i = 0; i <= maxOffset; i++) {
                int end = query.size() - i;
                results.addAll(match(query.getRange(end - k, end)));
            }
        }
        return results;
    }

    @Override
    public int getK() {
        return k;
    }
}
