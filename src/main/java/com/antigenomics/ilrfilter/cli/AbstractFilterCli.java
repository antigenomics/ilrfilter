package com.antigenomics.ilrfilter.cli;

import com.antigenomics.ilrfilter.*;
import com.milaboratory.core.io.sequence.*;
import com.milaboratory.core.io.sequence.fastq.PairedFastqReader;
import com.milaboratory.core.io.sequence.fastq.PairedFastqWriter;
import com.milaboratory.core.io.sequence.fastq.SingleFastqReader;
import com.milaboratory.core.io.sequence.fastq.SingleFastqWriter;
import io.repseq.core.VDJCGene;
import picocli.CommandLine;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public abstract class AbstractFilterCli implements Runnable {
    @CommandLine.Option(names = {"-I", "--input"},
            required = true,
            paramLabel = "<path/to/files>",
            arity = "1..2",
            description = "Path to input file(s), space-separated")
    protected List<String> inputPaths;

    @CommandLine.Option(names = {"-O", "--output"},
            required = true,
            paramLabel = "<path/prefix>",
            description = "Path and prefix for output files")
    protected String outputPrefix;

    @CommandLine.Option(names = {"-k", "--kmer-size"},
            defaultValue = "16",
            paramLabel = "[3,inf)",
            description = "K-mer size (default: ${DEFAULT-VALUE})")
    protected int kSize;

    @CommandLine.Option(names = {"-S", "--species"},
            required = true,
            paramLabel = "<id>",
            description = "Specify organism (allowed values: hsa, mmu)")
    protected String species;

    @CommandLine.Option(names = {"-s", "--max-substitutions"},
            defaultValue = "1",
            paramLabel = "[0,inf)",
            description = "Maximum number of substitutions allowed (default: ${DEFAULT-VALUE})")
    protected int maxSubstitutions;

    @CommandLine.Option(names = {"-l", "--limit"},
            defaultValue = "-1",
            paramLabel = "[1,inf)",
            description = "Number of reads to take. Will use all reads if not a positive number (default: ${DEFAULT-VALUE})")
    protected int limit;

    @CommandLine.Option(names = {"-t", "--threads"},
            defaultValue = "4mc",
            paramLabel = "[1,inf)",
            description = "Number of threads to use. Will use all available threads if not a positive number (default: ${DEFAULT-VALUE})")
    protected int threads;

    @CommandLine.Option(names = {"-mo", "--max-offset"},
            defaultValue = "1",
            paramLabel = "[0,inf)",
            description = "Max offset from read boundary (default: ${DEFAULT-VALUE})")
    protected int maxOffset;

    @CommandLine.Option(names = {"-ob", "--outer-boundary"},
            description = "Only match outer boundary of a read. I.e. start of R1 or end of R2. " +
                    "If not set, will check both ends of each reads in pair.")
    protected boolean outerBoundMatchOnly;

    protected abstract KMerMatcherFactory<VDJCGene, ? extends AbstractKmerMatcher<VDJCGene>> getMatcherFactory();

    @Override
    public void run() {
        try {
            threads = threads < 1 ? Runtime.getRuntime().availableProcessors() : threads;

            sout("Started processing " + inputPaths + ". Initializing K-mer library..");

            BoundarySegmentMatcher<VDJCGene, ? extends AbstractKmerMatcher<VDJCGene>> bsm = new BoundarySegmentMatcher<>(
                    getMatcherFactory(),
                    new RepseqioReferenceProvider(),
                    species,
                    maxOffset,
                    outerBoundMatchOnly
            );

            sout("K-mer library initialized.");

            if (inputPaths.size() == 2) {
                try (PairedFastqReader reader = new PairedFastqReader(
                        inputPaths.get(0),
                        inputPaths.get(1));
                     PairedFastqWriter writer = new PairedFastqWriter(
                             createPath("_R1.fastq", true),
                             createPath("_R2.fastq", false)
                     )) {
                    runPipeline(reader, writer, bsm.asPairedReadProcessor());
                }
            } else {
                try (SingleFastqReader reader = new SingleFastqReader(inputPaths.get(0));
                     SingleFastqWriter writer = new SingleFastqWriter(createPath("_R1.fastq", true))) {
                    runPipeline(reader, writer, bsm.asSingleReadProcessor());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final AtomicLong totalReads = new AtomicLong(), readsPassedFilter = new AtomicLong();

    private <R extends SequenceRead, T>
    void runPipeline(SequenceReaderCloseable<R> reader,
                     SequenceWriter<R> writer,
                     BsmProcessor<R, T> bsmProcessor) {
        Stream<R> stream = new SequenceReaderIterator<>(reader).parallelStream();

        if (limit > 0) {
            stream = stream.limit(limit);
        }

        stream.filter(x -> {
            boolean passes = bsmProcessor.process(x).isAnnotated();

            reportProgress(false, passes);

            return passes;
        }).forEach(writer::write);

        reportProgress(true, false);
    }

    private void reportProgress(boolean finished, boolean passes) {
        long total = finished ? totalReads.get() : totalReads.incrementAndGet(),
                passed = passes ? readsPassedFilter.incrementAndGet() : readsPassedFilter.get();

        if (finished || total % 1000000L == 0) {
            sout((finished ? "Finished processing " + inputPaths + ". " : "") +
                    "Processed N=" + total + " reads, p=" +
                    passed / (float) total + "(n=" + passed + ") reads passed filter.");
        }
    }

    private void makeFolders() {
        File targetPath = new File(createPath("tmp", false));
        File parent = targetPath.getParentFile();
        if (parent == null) {
            // Local path
            return;
        }
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Couldn't create dir: " + parent);
        }
    }

    protected String createPath(String suffix, boolean safe) {
        if (safe) {
            makeFolders();
        }

        return outputPrefix +
                (outputPrefix.endsWith(File.separator) ? "output" : "") +
                suffix;
    }

    protected void sout(String str) {
        System.out.println("[" + new Date() + "] " + str);
    }
}
