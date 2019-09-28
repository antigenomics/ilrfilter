package com.antigenomics.ilrfilter;

import org.junit.Assert;
import org.junit.Test;

public class RepseqioReferenceProviderTest {

    @Test
    public void getSegments() {
        var refs = new RepseqioReferenceProvider().getReferences("hsa");
        Assert.assertTrue(!refs.isEmpty());
        refs.stream().limit(10)
                .map(SequenceWithPayload::getPayload)
                .forEach(System.out::println);
    }
}