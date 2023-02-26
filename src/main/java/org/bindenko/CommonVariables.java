package org.bindenko;

import java.math.BigInteger;
import java.util.LinkedList;

public class CommonVariables {
    public static final BigInteger p = new BigInteger("1091659");
    public static final BigInteger q = new BigInteger("181943");
    public static final BigInteger g = new BigInteger("9");
    public static LinkedList<BigInteger> primeNumbers = new LinkedList<>();

    /**
     * Метод инициадизирует ряд простых чисел в зависимости от параметра
     *
     * @param upperLimit верхний предел ряда простых чисел (необязательно простой)
     */
    public static void initializePrimeNumbers(int upperLimit){
        BigInteger seed;
        BigInteger limit = new BigInteger(String.valueOf(upperLimit));
        BigInteger nextPrimeNumber = new BigInteger("2");

        for(; nextPrimeNumber.intValue() < limit.intValue(); nextPrimeNumber = seed.nextProbablePrime()){
            primeNumbers.add(nextPrimeNumber);
            seed = nextPrimeNumber;
        }
    }
}
