package com.antigenomics.ilrfilter.cli;

import com.antigenomics.ilrfilter.AbstractKmerMatcher;
import com.antigenomics.ilrfilter.HashMatcherFactory;
import com.antigenomics.ilrfilter.KMerMatcherFactory;
import io.repseq.core.VDJCGene;

public final class HashFilterCli extends AbstractFilterCli {
    @Override
    protected KMerMatcherFactory<VDJCGene, ? extends AbstractKmerMatcher<VDJCGene>> getMatcherFactory() {
        return new HashMatcherFactory<>(kSize, maxSubstitutions);
    }
}
