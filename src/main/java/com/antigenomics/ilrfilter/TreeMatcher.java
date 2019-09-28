package com.antigenomics.ilrfilter;

import com.milaboratory.core.sequence.NucleotideSequence;
import com.milaboratory.core.tree.SequenceTreeMap;
import com.milaboratory.core.tree.TreeSearchParameters;

import java.util.*;

public final class TreeMatcher<T> extends AbstractKmerMatcher<T> {
    private final SequenceTreeMap<NucleotideSequence, List<T>> stm = new SequenceTreeMap<>(NucleotideSequence.ALPHABET);
    private final TreeSearchParameters treeSearchParameters;

    public TreeMatcher(List<SequenceWithPayload<T>> records,
                       int k, TreeSearchParameters treeSearchParameters) {
        super(k);
        this.treeSearchParameters = treeSearchParameters;

        for (SequenceWithPayload<T> record : records) {
            T payload = record.getPayload();
            for (NucleotideSequence sequence : Arrays.asList(record.getSequence(),
                    record.getSequence().getReverseComplement())) {
                for (int i = 0; i <= sequence.size() - k; i++) {
                    stm.createIfAbsent(sequence.getRange(i, i + k), ArrayList::new).add(payload);
                }
            }
        }
    }

    @Override
    public Set<NucleotideSequence> getKmers() {
        return stm.toMap().keySet();
    }

    @Override
    protected List<T> matchInner(NucleotideSequence query) {
        return stm.getNeighborhoodIterator(query, treeSearchParameters).next();
    }

    @Override
    public String toString() {
        return "TreeMatcherFactory{" +
                "k=" + k +
                ", s=" + treeSearchParameters.getMaxSubstitutions() +
                ", i=" + treeSearchParameters.getMaxInsertions() +
                ", d=" + treeSearchParameters.getMaxDeletions() +
                ", t=" + (int) treeSearchParameters.getMaxPenalty() +
                '}';
    }
}
