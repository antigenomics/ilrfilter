package com.antigenomics.ilrfilter;

import io.repseq.core.VDJCGene;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class RepseqioReferenceProviderTest {

    @Test
    public void getSegments() {
        List<SequenceWithPayload<VDJCGene>> refs = new RepseqioReferenceProvider().getReferences("hsa");
        Assert.assertTrue(!refs.isEmpty());
        refs.stream().limit(10)
                .map(SequenceWithPayload::getPayload)
                .forEach(System.out::println);
    }
}