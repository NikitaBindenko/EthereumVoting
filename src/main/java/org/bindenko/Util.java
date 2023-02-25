package org.bindenko;

import java.math.BigInteger;

public class Util {
    public static BigInteger pow(BigInteger a, BigInteger b, BigInteger modulo){
        BigInteger counter = new BigInteger("0");
        BigInteger result = new BigInteger("1");
        for(; counter.compareTo(b) < 0; counter = counter.add(new BigInteger("1"))){
            result = result.multiply(a).mod(modulo);
        }
        return result;
    }

    BigInteger pow(BigInteger base, BigInteger exponent) {
        BigInteger result = BigInteger.ONE;
        while (exponent.signum() > 0) {
            if (exponent.testBit(0)) result = result.multiply(base);
            base = base.multiply(base);
            exponent = exponent.shiftRight(1);
        }
        return result;
    }
}
