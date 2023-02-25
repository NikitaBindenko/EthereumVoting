package org.bindenko;

import java.math.BigInteger;
import java.util.LinkedList;

public class CommonVariables {
    public static final BigInteger p = new BigInteger("1091659");
    public static final BigInteger q =  new BigInteger("181943");
    public static final BigInteger g = new BigInteger("9");
    public static LinkedList<BigInteger> primeNumbers = new LinkedList<>();

    public static void initializePrimeNumbers(){
        BigInteger seed;
        BigInteger limit = new BigInteger(String.valueOf(16));
        BigInteger nextPrimeNumber = new BigInteger("2");

        for(; nextPrimeNumber.intValue() < limit.intValue(); nextPrimeNumber = seed.nextProbablePrime()){
            primeNumbers.add(nextPrimeNumber);
            seed = nextPrimeNumber;
        }
    }
}
