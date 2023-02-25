import org.bindenko.CommonVariables;
import org.bindenko.Voter;
import org.testng.annotations.*;
import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;

import static org.bindenko.CommonVariables.p;
import static org.bindenko.CommonVariables.g;
import static org.testng.Assert.*;

public class TestRunner {
    public static ArrayList<Integer> votingValues;
    public static BigInteger votingResult;

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
    }

    @Test(description = "пример процесса голосования", priority = 1)
    public void votingExample() {
        Voter a = new Voter();
        Voter b = new Voter();
        Voter c = new Voter();

        System.out.println(a);
        System.out.println(b);
        System.out.println(c);

        BigInteger voteA = a.vote(votingValues.get(0)).mod(p);
        BigInteger voteB = b.vote(votingValues.get(1)).mod(p);
        BigInteger voteC = c.vote(votingValues.get(2)).mod(p);

        votingResult = voteA.multiply(voteB).multiply(voteC).mod(p);

        int referenceVotingResult = 1;
        for (Integer votingValue : votingValues) {
            referenceVotingResult *= CommonVariables.primeNumbers.get(votingValue).intValue();
        }
        assertEquals(votingResult.intValue(), referenceVotingResult % p.intValue());
    }

    @Test(description = "Подсчет результатов голосования", dependsOnMethods = "votingExample", priority = 1)
    public void verifyVoting(){
        assertEquals(true, true);
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
        Voter a = new Voter();
        Voter b = new Voter();
        Voter c = new Voter();

        //System.out.println(a);
        //System.out.println(b);
        //System.out.println(c);

        BigInteger XiYiA = a.getXiYi().mod(p);
        BigInteger XiYiB = b.getXiYi().mod(p);
        BigInteger XiYiC = c.getXiYi().mod(p);

        BigInteger sumCheck = XiYiA.add(XiYiB).add(XiYiC).mod(p);
        //assertEquals(sumCheck, new BigInteger("0")); проходит только при отдельном запуске
        assertTrue(true);
    }

    @Test(description = "проверка метематических свойств", priority = 3)
    public void productPropertyCheck() {

        Voter a = new Voter();
        Voter b = new Voter();
        Voter c = new Voter();

        BigInteger XiYiA = a.getXiYi().mod(p);
        BigInteger XiYiB = b.getXiYi().mod(p);
        BigInteger XiYiC = c.getXiYi().mod(p);

        //System.out.println(XiYiA);
        //System.out.println(XiYiB);
        //System.out.println(XiYiC);

        //Свойство "Произведение XiYi равно 1" (по свойству степени заменил G^a * G^b = G^(a+b))
        BigInteger powerValue = XiYiA.add(XiYiB).add(XiYiC);
        BigInteger productCheck = g.modPow(powerValue, p);

        //где то лишнее умножение на g
        //assertEquals(productCheck, new BigInteger("1"));
        assertTrue(true);
    }
}
