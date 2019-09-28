package com.antigenomics.ilrfilter;

import com.milaboratory.core.io.sequence.PairedRead;
import com.milaboratory.core.io.sequence.fastq.PairedFastqReader;
import com.milaboratory.core.tree.TreeSearchParameters;
import io.repseq.core.VDJCGene;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class BoundarySegmentMatcherTest {
    private List<PairedRead> reads = new ArrayList<>();

    public BoundarySegmentMatcherTest() throws IOException {
        PairedFastqReader pfr = new PairedFastqReader(TestUtils.getResourceAsStream("reads_R1.fastq"),
                TestUtils.getResourceAsStream("reads_R2.fastq"));
        PairedRead read;
        while ((read = pfr.take()) != null) {
            reads.add(read);
        }
    }

    @Test
    public void matchTest() {
        BoundarySegmentMatcher<VDJCGene, TreeMatcher<VDJCGene>> bsm = new BoundarySegmentMatcher<>(
                new TreeMatcherFactory<>(16, new TreeSearchParameters(1, 0, 0)),
                new RepseqioReferenceProvider(), "hsa", 0, true
        );
        //System.out.println(reads.get(0).getR1().getData().getSequence());
        var res1 = bsm.match(reads.get(0).getR1(), true);
        Assert.assertTrue(!res1.isEmpty());
        Assert.assertEquals("TRAJ57", cleanGeneName(res1.get(0).getName()));
        var res2 = bsm.match(reads.get(0).getR2(), true);
        Assert.assertTrue(!res2.isEmpty());
        Assert.assertEquals("TRAV38", cleanGeneName(res2.get(0).getName()));
        var res3 = bsm.match(reads.get(0).getR1(), false);
        Assert.assertTrue(!res3.isEmpty());
        Assert.assertEquals("TRAV38", cleanGeneName(res3.get(0).getName()));
        var res4 = bsm.match(reads.get(0).getR2(), false);
        Assert.assertTrue(!res4.isEmpty());
        Assert.assertEquals("TRAV38", cleanGeneName(res4.get(0).getName()));
    }

    private String cleanGeneName(String name) {
        return name.replaceAll("[*-].+", "");
    }

    @Test
    public void testFN() throws IOException {
        BoundarySegmentMatcher<VDJCGene, TreeMatcher<VDJCGene>> bsm = new BoundarySegmentMatcher<>(
                new TreeMatcherFactory<>(16, new TreeSearchParameters(1, 0, 0)),
                new RepseqioReferenceProvider(), "hsa", 0, false
        );

        int matches = 0;
        for (PairedRead read : reads) {
            var res = bsm.match(read);
            if (!res.isEmpty()) {
                matches++;
            }
            //System.out.println(res);
        }

        System.out.println("Matched " + matches + " out of " + reads.size() + " reads");
        Assert.assertEquals(reads.size(), matches);

        //System.out.println(reads.get(6).getR1().getData().getSequence());
        //System.out.println(reads.get(6).getR2().getData().getSequence());
        //System.out.println(bsm.match(reads.get(6)));
    }

    @Test
    public void testSpeedAndFP() {
        randomTestFP(new HashMatcherFactory(16, 1));
        randomTestFP(new TreeMatcherFactory(16, new TreeSearchParameters(1, 0, 0)));
        //randomTestFPR(new TreeMatcherFactory(16, new TreeSearchParameters(2, 0, 0)));
    }

    private void randomTestFP(KMerMatcherFactory matcherFactory) {
        long startTime = System.currentTimeMillis();
        BoundarySegmentMatcher boundarySegmentMatcher = new BoundarySegmentMatcher(
                matcherFactory,
                new RepseqioReferenceProvider(), "hsa", 0, true
        );
        var matcher = boundarySegmentMatcher.getMatcher();
        long timeElapsedMillis = (System.currentTimeMillis() - startTime);
        System.out.println("Created " + matcher + " matcher with m=" +
                matcher.getKmers().size() + "k-mers in dt=" +
                timeElapsedMillis + "ms");

        int matched = 0, runs = 1000000;
        startTime = System.currentTimeMillis();
        for (int i = 0; i < runs; i++) {
            if (!matcher.match(TestUtils.randomSequence(matcher.getK())).isEmpty()) {
                matched++;
            }
        }
        timeElapsedMillis = (System.currentTimeMillis() - startTime);
        System.out.println("Matched p=" + matched / (double) runs + " of n=" + runs +
                " random sequences in dt=" + timeElapsedMillis + "ms");
    }
}