package com.antigenomics.ilrfilter;

import com.milaboratory.core.sequence.NucleotideSequence;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.impl.map.mutable.primitive.LongObjectHashMap;

import java.util.*;

public final class HashMatcher<T> extends AbstractKmerMatcher<T> {
    private final MutableLongObjectMap<List<T>> kmerHashMap = new LongObjectHashMap<>();
    private final int mismatches;

    public HashMatcher(List<SequenceWithPayload<T>> records, int k, int mismatches) {
        super(k);
        this.mismatches = mismatches;

        if (k < 3 || k > 31)
            throw new IllegalArgumentException("K-mer length should be in [3, 31] (64bit)");

        if (mismatches > 2) {
            throw new IllegalArgumentException("At most 2 mismatches are allowed");
        }

        records.forEach(record -> {
            T payload = record.getPayload();
            NucleotideSequence seq = record.getSequence(),
                    seqRc = record.getSequence().getReverseComplement();
            for (int i = 0; i <= seq.size() - k; i++) {
                put(seq.getRange(i, i + k), payload, mismatches);
                put(seqRc.getRange(i, i + k), payload, mismatches);
            }
        });
    }

    void put(NucleotideSequence subsequence, T payload, int mismatches) {
        kmerHashMap.getIfAbsentPut(getKmer(subsequence), ArrayList::new).add(payload);

        if (mismatches > 0) {
            byte[] data = subsequence.asArray();
            for (int i = 0; i < subsequence.size(); i++) {
                byte current = data[i];
                for (byte k = 0; k < 4; k++) {
                    if (k != current) {
                        data[i] = k;
                        put(new NucleotideSequence(data), payload, mismatches - 1);
                    }
                }
                data[i] = current;
            }
        }
    }

    public int getMismatches() {
        return mismatches;
    }

    long getKmer(NucleotideSequence sequence) {
        long kmer = 0;
        for (int i = 0; i < k; ++i) {
            kmer = kmer << 2 | (sequence.codeAt(i) & 3); // avoid N's
        }
        return kmer;
    }

    NucleotideSequence getSequence(long kmer) {
        byte[] seq = new byte[k];
        for (int i = 1; i <= k; ++i) {
            seq[k - i] = (byte) (kmer & 3);
            kmer = kmer >> 2;
        }
        return new NucleotideSequence(seq);
    }


    @Override
    protected List<T> matchInner(NucleotideSequence query) {
        return kmerHashMap.get(getKmer(query.getRange(0, k)));
    }

    @Override
    public Set<NucleotideSequence> getKmers() {
        return kmerHashMap.keySet().collect(this::getSequence);
    }

    @Override
    public String toString() {
        return "HashMatcher{" +
                "k=" + k + ",mm=" + mismatches +
                '}';
    }
}
