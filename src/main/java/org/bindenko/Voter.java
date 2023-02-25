package org.bindenko;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static org.bindenko.CommonVariables.*;

public class Voter {
    private final BigInteger pubKey;
    private final BigInteger privateKey;
    public static HashMap<BigInteger, Voter> votersPubKeys = new HashMap<>();

    /**
     * Конструктор генерирует закрытый и открытый ключи,
     * добавляет свой публичный ключ и себя в HashMap,
     * хранящий список соответствий ключ-участник
     * для всех участников
     */
    public Voter(){
        privateKey = new BigInteger(256, new Random()).mod(q);
        pubKey = g.pow(privateKey.intValue()).mod(p);
        votersPubKeys.put(this.pubKey, this);
    }

    /**
     * Для тестирования
     */
    public Voter(BigInteger privateKey){
        this.privateKey = privateKey;
        this.pubKey = g.pow(privateKey.intValue()).mod(p);
        votersPubKeys.put(this.pubKey, this);
    }

    /**
     * Метод позволяет другим участникам голосования получить данные для
     * неинтерактивного доказательства нулевого разглашения.
     * Используется для верификации остальных участников голосования.
     *
     * @return ArrayList = [s, bigK, c]
     */
    public ArrayList<BigInteger> getNIZKP(){
        ArrayList<BigInteger> dataToVerifyNIZKP = new ArrayList<>(3);
        try {
            //Вычислил k и K (1 шаг)
            BigInteger k = new BigInteger(256, new Random()).mod(q);
            //BigInteger k = new BigInteger("11");
            BigInteger bigK = g.pow(k.intValue()).mod(p);

            //сгенерировал c сам (отличие от интерактивной процедуры)(2 шаг)
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(bigK.toByteArray());
            BigInteger c = new BigInteger(hash).mod(p);

            //Вычислил s (3 шаг)
            BigInteger s = privateKey.multiply(c).add(k).mod(q);

            dataToVerifyNIZKP.add(s);
            dataToVerifyNIZKP.add(bigK);
            dataToVerifyNIZKP.add(c);

        }catch (NoSuchAlgorithmException e){
            System.out.println("Неподдерживаемый алгоритм хэширования!");
        }
        return dataToVerifyNIZKP;
    }

    /**
     * Метод позволяет проверить выполнить неинтерактивное
     * доказательство нулевого разглашения другого участника.
     * Используется для верификации остальных участников голосования.
     *
     * @return boolean
     */
    public boolean verifyNIZKP(Voter otherVoter){
        ArrayList<BigInteger> dataToVerify = otherVoter.getNIZKP();
        BigInteger s = dataToVerify.get(0);
        BigInteger bigK = dataToVerify.get(1);
        BigInteger c = dataToVerify.get(2);

        //вычисление выражения и проверка валидности NIZKP
        BigInteger leftPart = g.pow(s.intValue()).mod(q);
        BigInteger rightPartX = otherVoter.getPubKey().pow(c.intValue()).mod(q);
        BigInteger rightPart = rightPartX.multiply(bigK).mod(q);
        return leftPart.equals(rightPart);
    }

    /**
     * Метод позволяет получить данные
     * для валидации голоса другого участника.
     *
     * @return boolean
     */
    public ArrayList<BigInteger> getVotingValueZKP(Voter otherVoter){
         ArrayList<BigInteger> dataToVerifyVotingValues = new ArrayList<>(3);

         return dataToVerifyVotingValues;
    }

    /**
     * Метод позволяет выполнить
     * валидацию голоса другого участника.
     *
     * @return boolean
     */
    public boolean verifyVotingValueZKP(Voter otherVoter){
         return true;
    }

    /**
     * Метод позволяет задать значение голоса для участника голосования
     *
     * @param votingValue - простое число, соответствующее номеру кандидата,
     * то есть принадлежащее множеству {2, 3, 5, 7,...} в случае голосования
     * за кандидата {0, 1, 2, 3,...} соответственно.
     *
     * @return BigInteger зашифрованное значение голоса
     */
    public BigInteger vote(int votingValue){
        BigInteger v = CommonVariables.primeNumbers.get(votingValue);
        BigInteger GpowXiYi = calculateGpowYi().modPow(this.privateKey, p);
        return v.multiply(GpowXiYi).mod(p);
    }

    /**
     * Метод позволяет выполнить подсчет голосов
     *
     * @param votingResult - результат голосования. Произведение простых чисел,
     *                     каждое из которых соответствует номеру кандидата,
     *                     за которого проголосовал один из участников
     *
     * @return ArrayList
     */
    public ArrayList<Integer> tally(int votingResult){
        ArrayList<Integer> ballotDistribution = new ArrayList<>(primeNumbers.size());


        return ballotDistribution;
    }

    /**
     * Метод позволяет вычислить порождающий многочлен в степени Y.
     * Вычисление используется на втором шаге алгоритма для вычисления произведения
     * и вынесено в отдельный метод для улучшения читаемости кода
     *
     * @return BigInteger g в степени y
     */
    private BigInteger calculateGpowYi(){
        BigInteger multiplication = new BigInteger("1");
        for(Voter voter : votersPubKeys.values()){
            if(voter.getPubKey().compareTo(this.pubKey) < 0){ //если ключи меньше текущего
                multiplication = multiplication.multiply(voter.getPubKey()).mod(p);
            } else if (voter.getPubKey().compareTo(this.pubKey) > 0) { //если ключи больше текущего
                multiplication = multiplication.multiply(voter.getGpowQminusX()).mod(p);
            }
            else{   //свой индекс (ключ)
                continue;
            }
        }
        return multiplication.mod(p);
    }

    /**
     * Проверка Proposition 1
     * (не подходит для реализации схемы, так как используются закрытые ключи других участников)
     * Сумма всех XiYi равна 0
     *
     * @return XiYi
     */
    public BigInteger getXiYi(){
        BigInteger sumMoreThenThis = new BigInteger("0");
        BigInteger sumLessThenThis = new BigInteger("0");
        for(Voter voter : votersPubKeys.values()){
            if(voter.getPubKey().compareTo(this.pubKey) < 0){ //если ключи меньше текущего
                sumLessThenThis = sumLessThenThis.add(voter.privateKey);
            } else if (voter.getPubKey().compareTo(this.pubKey) > 0) { //если ключи больше текущего
                sumMoreThenThis = sumMoreThenThis.add(voter.privateKey);
            }
            else{   //свой индекс (ключ)
                continue;
            }
        }
        return sumLessThenThis.subtract(sumMoreThenThis).multiply(this.privateKey);
    }

    /**
     * Метод позволяет вычислить порождающий многочлен в степени q - x.
     * Вычисление используется на втором шаге алгоритма для вычисления произведения
     * и вынесено в отдельный метод для улучшения читаемости кода
     *
     * @return BigInteger g в степени y
     */
    public BigInteger getGpowQminusX(){
        BigInteger QminusX = q.subtract(privateKey);
        return g.modPow(QminusX, p);
    }

    public BigInteger getPubKey(){
        return pubKey;
    }

    @Override
    public String toString() {
        return "Voter{" +
                "pubKey=" + pubKey +
                ", privateKey=" + privateKey +
                '}';
    }
}
