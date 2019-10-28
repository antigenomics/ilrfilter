### Pre-filtering of immunoglobulin-like reads

Speed up TCR/BCR mapping for large FASTQ files like the ones coming from RNA-Seq experiments. Inspired by Vidjil algorithm.

You can get latest JAR from releases section, required Java 1.8+ to run. Run as

```
java -jar ilrfilter-0.0.1.jar hash -S hsa -I reads_R1.fastq reads_R2.fastq -O out_prefix
```

To see the list of available options run either

```
java -jar ilrfilter-0.0.1.jar hash
```

for hashmap-based (kmer) algorithm or

```
java -jar ilrfilter-0.0.1.jar tree
```

for tree-based algorithm. To compile and check clone the repo and run ``test.sh`` in ``examples/`` folder.

Tree-based algorithm is slower but takes less memory and startup time than hash-based.

Note that we found that using a K-mer of length 15 with 1 mismatch (default parameters for hash-based algorithm) allows reducing data size ~10 to 50-fold while having a false-negative rate < 0.1%.
Selecting longer K-mers or more mismatches for hash-based algorhtm, or using several substitutions and indels for tree-based algorithm can significantly increase running 
time/memory requirements and lead to no filtering of input file.

Also note that this implementation uses Java Stream API from 1.8 so it will use all available cores by default.