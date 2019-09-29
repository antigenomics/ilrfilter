package com.antigenomics.ilrfilter.cli;

import com.milaboratory.core.io.sequence.*;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class SequenceReaderIterator<T extends SequenceRead>
        implements Iterator<T>, AutoCloseable {

    private final SequenceReaderCloseable<T> reader;
    private T read;

    public SequenceReaderIterator(SequenceReaderCloseable<T> reader) {
        this.reader = reader;
    }

    @Override
    public boolean hasNext() {
        if ((read = reader.take()) == null) {
            close();
            return false;
        }
        return true;
    }

    @Override
    public T next() {
        return read;
    }

    @Override
    public void close() {
        reader.close();
    }

    public Iterable<T> iterable() {
        return () -> this;
    }

    public Stream<T> stream() {
        return StreamSupport.stream(iterable().spliterator(), false);
    }

    public Stream<T> parallelStream() {
        /*
        TODO
         Despite their obvious utility in parallel algorithms, spliterators are not expected to be thread-safe;
         instead, implementations of parallel algorithms using spliterators should ensure that the spliterator
         is only used by one thread at a time. This is generally easy to attain via serial thread-confinement,
         which often is a natural consequence of typical parallel algorithms that work by recursive decomposition.
         */
        return StreamSupport.stream(iterable().spliterator(), true);
    }
}
