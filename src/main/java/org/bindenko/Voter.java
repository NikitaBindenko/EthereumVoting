package org.bindenko;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import static org.bindenko.CommonVariables.*;

public class Voter {
    private final BigInteger pubKey;
    private final BigInteger privateKey;
    private BigInteger myVotingValue;
    public static HashMap<BigInteger, Voter> votersPubKeys = new HashMap<>();
    public LinkedList<Voter> failedVoters = new LinkedList<>();

    /**
     * Конструктор генерирует закрытый и открытый ключи,
     * добавляет свой публичный ключ и себя в HashMap,
     * хранящий список соответствий ключ-участник
     * для всех участников
     */
    public Voter(){
        privateKey = new BigInteger(256, new Random()).mod(q);
        pubKey = g.modPow(privateKey, p);
        votersPubKeys.put(this.pubKey, this);
        myVotingValue = BigInteger.ONE;
    }

    /**
     * Для тестирования
     */
    public Voter(BigInteger privateKey){
        this.privateKey = privateKey;
        this.pubKey = g.modPow(privateKey, p);
        votersPubKeys.put(this.pubKey, this);
        myVotingValue = BigInteger.ONE;
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
            BigInteger bigK = g.modPow(k, p);

            //сгенерировал c сам (отличие от интерактивной процедуры)(2 шаг)
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(bigK.toByteArray());
            BigInteger c = new BigInteger(hash).mod(q);
            //Вычислил s (3 шаг)
            BigInteger s = q.subtract(privateKey).multiply(c).add(k).mod(q);

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
        BigInteger SchnorrRight = g.modPow(s, p).multiply(otherVoter.getPubKey().modPow(c, p)).mod(p);
        return bigK.equals(SchnorrRight);
    }

    /**
     * Метод позволяет получить данные
     * для валидации голоса другого участника.
     *
     * @return порождающий многочлен в степени Xi * Yi
     */
    public BigInteger getVotingValueZKP(){
        BigInteger XiYi = this.getXiYi();
        return g.modPow(XiYi, p);
    }

    /**
     * Метод позволяет выполнить
     * валидацию голоса другого участника.
     * Нужно исправить основное выражение в цикле и сделать без использования приватного ключа
     * (кажется что это работает только для голосования "за" или "против")
     *
     * @return boolean
     */
    public boolean verifyVotingValuesZKP(){
        BigInteger productionResult = BigInteger.ONE;
        for(Voter otherVoter : votersPubKeys.values()){
            productionResult = productionResult.multiply(otherVoter.getVotingValueZKP());
        }
        return productionResult.mod(p).equals(BigInteger.ONE);
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
        if(votingValue < 0){
            myVotingValue = BigInteger.ONE;
            return myVotingValue;
        }
        BigInteger v = CommonVariables.primeNumbers.get(votingValue);
        BigInteger GpowXiYi = calculateGpowYi().modPow(this.privateKey, p);
        this.myVotingValue = v.multiply(GpowXiYi);
        return myVotingValue;
    }

    /**
     * Метод позволяет выполнить подсчет голосов
     *
     * @param votingResult - результат голосования. Произведение простых чисел,
     *                     каждое из которых соответствует номеру кандидата,
     *                     за которого проголосовал один из участников
     *
     * @return массив количеств голосов за каждого участника
     */
    public int[] tally(BigInteger votingResult){
        BigInteger zero = BigInteger.ZERO;
        int[] ballotDistribution = new int[primeNumbers.size()];
        for (int i = 0; i < primeNumbers.size(); i++) {
            while(votingResult.divideAndRemainder(primeNumbers.get(i))[1].equals(zero)){
                votingResult = votingResult.divide(primeNumbers.get(i));
                ballotDistribution[i]++;
            }
        }
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
        BigInteger multiplication = BigInteger.ONE;
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
        BigInteger sumMoreThenThis = BigInteger.ZERO;
        BigInteger sumLessThenThis = BigInteger.ZERO;
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

    /**
     * Метод позволяет получить произведение дополнительных значений для участника голосования
     * для восстановления результата голосования в случае если кто то из участников не проголосовал
     * для восстановления результата голосования метод должен быть вызван каждым участником
     * у всех проголосовавших участников, таким образом иммитируется широковещательное распространение.
     * После распространения значения умножаются друг на друга каждым участником
     *
     * @return произведение AdditionalValue для одного из участников
     */
    public BigInteger getAdditionalValueToRecoverTallyValue(LinkedList<Voter> failedVoters){
        BigInteger result = BigInteger.ONE;
        for(Voter failedVoter : failedVoters) {
            BigInteger additionalValue = BigInteger.ONE;
            if (failedVoter.getPubKey().compareTo(this.pubKey) > 0) { //тут аккуратно меньше\больше
                additionalValue = failedVoter.getPubKey().modPow(this.privateKey, p);
            } else if (failedVoter.getPubKey().compareTo(this.pubKey) < 0) {
                BigInteger QminusXi = q.subtract(privateKey);
                additionalValue = failedVoter.getPubKey().modPow(QminusXi, p);
            }
            result = result.multiply(additionalValue);
        }
        return result;
    }

    /**
     * Метод позволяет вычислить первоначальный tallyValue
     *
     * @return tallyValue
     */
    private BigInteger getTallyValueWithFails(){
        BigInteger tallyValue = BigInteger.ONE;
        for(Voter voter : votersPubKeys.values()){
            BigInteger votingValue = voter.getVotingValue();
            tallyValue = tallyValue.multiply(votingValue);
            if(votingValue.equals(BigInteger.ONE)){
                failedVoters.add(voter);
            }
        }
        return tallyValue.mod(p);
    }

    /**
     * Метод позволяет вычислить tallyValue, и, если кому то из участников
     * не удалось проголосовать, собрать additionalValue от всех участников голосования
     * и перемножить их, тем самым восстанавливая результат голосования
     *
     * @return validTallyValue
     */
    public BigInteger getValidTallyValue(){
        BigInteger recoveredResult = BigInteger.ONE;
        BigInteger tallyValueWithFails = getTallyValueWithFails();
        if(failedVoters.size() != 0) { //если есть провалившиеся участники восстанавливаем
            for (Voter voter : votersPubKeys.values()) {
                if (voter.getVotingValue().equals(BigInteger.ONE)) {
                    continue;
                }
                recoveredResult =
                        recoveredResult.multiply(voter.getAdditionalValueToRecoverTallyValue(failedVoters));
            }
            return recoveredResult.multiply(tallyValueWithFails).mod(p);
        }else{
            return tallyValueWithFails;
        }
    }

    public BigInteger getVotingValue(){
        return myVotingValue;
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
