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
     * @param numberOfCandidates количество кандидатов, для которых будут сгенерированы
     *                              соответствующие простые числа
     */
    public static void initializePrimeNumbers(int numberOfCandidates){
        BigInteger seed = BigInteger.ONE;
        int i = 0;
        while(i < numberOfCandidates){
            BigInteger nextPrimeNumber = seed.nextProbablePrime();
            primeNumbers.add(nextPrimeNumber);
            seed = nextPrimeNumber;
            i++;
        }
    }
}
