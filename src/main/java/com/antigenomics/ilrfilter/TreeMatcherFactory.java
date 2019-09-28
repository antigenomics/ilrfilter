package com.antigenomics.ilrfilter;

import com.milaboratory.core.tree.TreeSearchParameters;

import java.util.List;

public final class TreeMatcherFactory<T> implements KMerMatcherFactory<T, TreeMatcher<T>> {
    private final int k;
    private final TreeSearchParameters treeSearchParameters;

    public TreeMatcherFactory(int k, TreeSearchParameters treeSearchParameters) {
        this.k = k;
        this.treeSearchParameters = treeSearchParameters;
    }

    @Override
    public TreeMatcher<T> create(List<SequenceWithPayload<T>> reference) {
        return new TreeMatcher<>(reference, k, treeSearchParameters);
    }

    public TreeSearchParameters getTreeSearchParameters() {
        return treeSearchParameters;
    }

    @Override
    public int getK() {
        return k;
    }
}
