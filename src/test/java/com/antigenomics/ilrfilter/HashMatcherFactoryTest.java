package com.antigenomics.ilrfilter;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class HashMatcherFactoryTest {
    @Test
    public void create() {
        var fact = new HashMatcherFactory<String>(6, 1);
        assertEquals(6 * 3 * 2 /*muts*/ + 2 /*orig +/-strand */, fact.create(
                List.of(new SequenceWithPayloadImpl("AAAAAA", "1"))
        ).getKmers().size());
    }
}