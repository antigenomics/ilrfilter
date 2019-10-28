package com.antigenomics.ilrfilter;

import org.junit.Test;

import java.math.BigInteger;

public class SandboxTests {
    @Test
    public void test() {
        long a = 0;
        print(a);

        print(a | (byte)3);

        print((a | (byte)3) & 3);

        print((a | (byte)2) << 2);
        print( (((a | (byte)2) << 2) >> 2) & 3);

        System.out.println( 1 << 25);
    }

    private void print(long x) {
        System.out.println(String.format("%064d", new BigInteger(Long.toBinaryString(x))));
    }
}
