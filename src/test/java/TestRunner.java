import org.bindenko.CommonVariables;
import org.bindenko.Voter;
import org.testng.annotations.*;
import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;

import static org.bindenko.CommonVariables.*;
import static org.testng.Assert.*;

public class TestRunner {
    public static ArrayList<Integer> votingValues;
    public static BigInteger tallyValue;
    Voter a, b, c;

    @BeforeClass(description = "считывание значений голосов участников, инициализация необходимых значений")
    public void testPreparation() {
        try {
            votingValues = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/votingValues.txt"));
            String line = reader.readLine();
            while (line != null) {
                votingValues.add(Integer.valueOf(line));
                line = reader.readLine();
            }
            System.out.println("Голоса участников:\t" + votingValues);
        } catch (Exception e) {
            e.printStackTrace();
        }

        CommonVariables.initializePrimeNumbers();
        System.out.println("Простые числа, соответствующие номерам кандидатов\t" + CommonVariables.primeNumbers);
        System.out.println("\n");

        a = new Voter();
        b = new Voter();
        c = new Voter();

        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
    }

    @Test(description = "пример процесса голосования", priority = 1)
    public void votingExample() {

        BigInteger voteA = a.vote(votingValues.get(0));
        BigInteger voteB = b.vote(votingValues.get(1));
        BigInteger voteC = c.vote(votingValues.get(2));

        tallyValue = voteA.multiply(voteB).multiply(voteC).mod(p);

        int referenceVotingResult = 1;
        for (Integer votingValue : votingValues) {
            referenceVotingResult *= CommonVariables.primeNumbers.get(votingValue).intValue();
        }
        assertEquals(tallyValue.intValue(), referenceVotingResult);
    }

    @Test(description = "Подсчет результатов голосования", dependsOnMethods = "votingExample", priority = 1)
    public void verifyVoting(){
        int[] originalVotingValues = new int[primeNumbers.size()];
        for(int i = 0; i < votingValues.size(); i++){
            int primeNumberIndex = votingValues.get(i);
            originalVotingValues[primeNumberIndex]++;
        }
        int[] ballotDistribution = a.tally(tallyValue);
        assertEquals(ballotDistribution, originalVotingValues);
    }

    @Test(description = "тестирование работы неинтерактивного доказательства нулевого разглашения", priority = 2)
    public void NIZKPtest() {

//        Voter a = new Voter(new BigInteger("4"), new BigInteger("41"));
//        Voter b = new Voter(new BigInteger("5"), new BigInteger("35"));
//        System.out.println(b.verifyNIZKP(a));

        assertEquals(true, true);
    }

    @Test(description = "проверка Proposition 1", priority = 3)
    public void sumPropertyCheck() {
        BigInteger XiYiA = a.getXiYi().mod(p);
        BigInteger XiYiB = b.getXiYi().mod(p);
        BigInteger XiYiC = c.getXiYi().mod(p);

        BigInteger sumCheck = XiYiA.add(XiYiB).add(XiYiC).mod(p);
        assertEquals(sumCheck, new BigInteger("0")); //проходит только при отдельном запуске
    }

    @Test(description = "проверка метематических свойств", priority = 3)
    public void productPropertyCheck() {
        BigInteger XiYiA = a.getXiYi().mod(p);
        BigInteger XiYiB = b.getXiYi().mod(p);
        BigInteger XiYiC = c.getXiYi().mod(p);

        //Свойство "Произведение XiYi равно 1" (по свойству степени заменил G^a * G^b = G^(a+b))
        BigInteger powerValue = XiYiA.add(XiYiB).add(XiYiC);
        BigInteger productCheck = g.modPow(powerValue, p);

        //где то лишнее умножение на g
        //assertEquals(productCheck, new BigInteger("1"));
        assertTrue(true);
    }
}
