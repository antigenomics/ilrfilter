package com.antigenomics.ilrfilter;

import com.milaboratory.core.tree.TreeSearchParameters;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TreeMatcherFactoryTest {

    @Test
    public void create() {
        var fact = new TreeMatcherFactory<String>(6, new TreeSearchParameters(0, 0, 0));
        assertEquals(2, fact.create(
                List.of(new SequenceWithPayloadImpl<>("AAAAAA", "1"))
        ).getKmers().size());
    }
}