package com.antigenomics.ilrfilter.cli;

import cc.redberry.pipe.CUtils;
import cc.redberry.pipe.blocks.ParallelProcessor;
import cc.redberry.pipe.util.CountLimitingOutputPort;
import com.antigenomics.ilrfilter.*;
import com.milaboratory.core.io.sequence.*;
import com.milaboratory.core.io.sequence.fastq.PairedFastqReader;
import com.milaboratory.core.io.sequence.fastq.PairedFastqWriter;
import com.milaboratory.core.io.sequence.fastq.SingleFastqReader;
import com.milaboratory.core.io.sequence.fastq.SingleFastqWriter;
import com.milaboratory.util.CanReportProgress;
import com.milaboratory.util.SmartProgressReporter;
import io.repseq.core.VDJCGene;
import picocli.CommandLine;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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
            defaultValue = "-1",
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

    static final boolean USE_CC_REDBERRY_PIPE = true;

    @Override
    public void run() {
        try {
            threads = threads < 1 ? Runtime.getRuntime().availableProcessors() : threads;

            sout("Started processing " + inputPaths + ". Initializing K-mer library..");

            var bsm = new BoundarySegmentMatcher<>(
                    getMatcherFactory(),
                    new RepseqioReferenceProvider(),
                    species,
                    maxOffset,
                    outerBoundMatchOnly
            );

            sout("K-mer library initialized.");

            if (inputPaths.size() == 2) {
                try (var reader = new PairedFastqReader(
                        inputPaths.get(0),
                        inputPaths.get(1));
                     var writer = new PairedFastqWriter(
                             createPath("_R1.fastq", true),
                             createPath("_R2.fastq", false)
                     )) {
                    runPipeline(reader, writer, bsm.asPairedReadProcessor());
                }
            } else {
                try (var reader = new SingleFastqReader(inputPaths.get(0));
                     var writer = new SingleFastqWriter(createPath("_R1.fastq", true))) {
                    runPipeline(reader, writer, bsm.asSingleReadProcessor());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <R extends SequenceRead, T>
    void runPipelineJavaStream(SequenceReaderCloseable<R> reader,
                               SequenceWriter<R> writer,
                               BsmProcessor<R, T> bsmProcessor) {
        AtomicLong totalReads = new AtomicLong(), readsPassedFilter = new AtomicLong();

        var stream = new SequenceReaderIterator<>(reader)
                .parallelStream();

        if (limit > 0) {
            stream = stream.limit(limit);
        }

        stream.filter(x -> {
            boolean passes = !bsmProcessor.process(x).isAnnotated();

            long total = totalReads.incrementAndGet(),
                    passed = passes ? readsPassedFilter.incrementAndGet() : readsPassedFilter.get();

            if (total % 500000L == 0) {
                sout("Processed N=" + total + " reads, p=" +
                        passed / (float) total + "(n=" + passed + ") reads passed filter.");
            }

            return passes;
        }).forEach(writer::write);

        long total = totalReads.get(),
                passed = readsPassedFilter.get();
        sout("Finished processing " + inputPaths + ". Processed N=" + total + " reads, p=" +
                passed / (float) total + "(n=" + passed + ") reads passed filter.");
    }

    private <R extends SequenceRead, T, I extends SequenceReaderCloseable<R> & CanReportProgress>
    void runPipelineRedberry(I reader,
                             SequenceWriter<R> writer,
                             BsmProcessor<R, T> bsmProcessor) {
        SmartProgressReporter.startProgressReport("Processing", reader);

        var input = limit > 0 ?
                new CountLimitingOutputPort<>(reader, limit) : reader;

        var result = new ParallelProcessor<>(input, bsmProcessor, threads);

        for (AnnotatedRead<R, T> annotatedRead : CUtils.it(result)) {
            if (annotatedRead.isAnnotated()) {
                writer.write(annotatedRead.getRead());
            }
        }

    }

    private <R extends SequenceRead, T, I extends SequenceReaderCloseable<R> & CanReportProgress>
    void runPipeline(I reader,
                     SequenceWriter<R> writer,
                     BsmProcessor<R, T> bsmProcessor) {
        if (USE_CC_REDBERRY_PIPE) {
            runPipelineRedberry(reader, writer, bsmProcessor);
        } else {
            runPipelineJavaStream(reader, writer, bsmProcessor);
        }
    }

    private void makeFolders() {
        var targetPath = new File(createPath("tmp", false));
        var parent = targetPath.getParentFile();
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
