package com.antigenomics.ilrfilter;

import com.milaboratory.core.sequence.NucleotideSequence;
import io.repseq.core.GeneFeature;
import io.repseq.core.VDJCGene;
import io.repseq.core.VDJCLibrary;
import io.repseq.core.VDJCLibraryRegistry;

import java.util.ArrayList;
import java.util.List;

public final class RepseqioReferenceProvider implements ReferenceProvider<VDJCGene> {
    @Override
    public List<SequenceWithPayload<VDJCGene>> getReferences(String species) {
        VDJCLibraryRegistry reg = VDJCLibraryRegistry.getDefault();

        reg.loadAllLibraries("default");

        long taxonFilter = reg.resolveSpecies(species);

        List<SequenceWithPayload<VDJCGene>> results = new ArrayList<>();

        for (VDJCLibrary lib : reg.getLoadedLibraries()) {
            if (taxonFilter != lib.getTaxonId())
                continue;

            for (VDJCGene gene : lib.getGenes()) {
                for (GeneFeature geneFeature : List.of(GeneFeature.VRegion, GeneFeature.JRegion, GeneFeature.CRegion)) {
                    NucleotideSequence featureSequence = gene.getFeature(geneFeature);

                    if (featureSequence == null)
                        continue;

                    results.add(new SequenceWithPayloadImpl<>(featureSequence, gene));
                }
            }
        }
        return results;
    }
}
