package com.antigenomics.ilrfilter.cli;

import com.antigenomics.ilrfilter.AbstractKmerMatcher;
import com.antigenomics.ilrfilter.KMerMatcherFactory;
import com.antigenomics.ilrfilter.TreeMatcherFactory;
import com.milaboratory.core.tree.TreeSearchParameters;
import io.repseq.core.VDJCGene;
import picocli.CommandLine;

public final class TreeFilterCli extends AbstractFilterCli {
    @CommandLine.Option(names = {"-i", "--max-insertions"},
            defaultValue = "0",
            paramLabel = "[0,inf)",
            description = "Maximum number of insertions allowed (default: ${DEFAULT-VALUE})")
    protected int maxInsertions;

    @CommandLine.Option(names = {"-d", "--max-deletions"},
            defaultValue = "0",
            paramLabel = "[0,inf)",
            description = "Maximum number of deletions allowed (default: ${DEFAULT-VALUE})")
    protected int maxDeletions;

    @CommandLine.Option(names = {"-m", "--max-mismatches"},
            defaultValue = "1",
            paramLabel = "[0,inf)",
            description = "Maximum number of mismatches (substitutions and indels) allowed (default: ${DEFAULT-VALUE})")
    protected int maxMismatches;

    @Override
    protected KMerMatcherFactory<VDJCGene, ? extends AbstractKmerMatcher<VDJCGene>> getMatcherFactory() {
        return new TreeMatcherFactory<>(kSize, new TreeSearchParameters(maxSubstitutions,
                maxDeletions, maxInsertions, maxMismatches));
    }
}
