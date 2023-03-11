package org.bindenko;

import java.util.ArrayList;
import java.math.BigInteger;
import java.util.LinkedList;

import static org.bindenko.CommonVariables.p;

public class PerformNoVotesFailure {
    private final ArrayList<Voter> voters;
    private final ArrayList<Integer> votingValues;
    private LinkedList<Voter> failedVoters = new LinkedList<>();

    public PerformNoVotesFailure(ArrayList<Voter> voters, ArrayList<Integer> votingValues){
        this.voters = voters;
        this.votingValues = votingValues;
    }

    /**
     * Метод умножает значения полученных голосов формируя тем самым результат голосования
     */
    public BigInteger votingSimulationWithoutSomebody(){
        BigInteger tallyValue = BigInteger.ONE;
        for(Voter voter : voters){
            BigInteger votingValue = voter.getVotingValue();
            tallyValue = tallyValue.multiply(votingValue);
            if(votingValue.equals(BigInteger.ONE)){
                failedVoters.add(voter);
            }
        }
        return tallyValue.mod(CommonVariables.p);
    }

    /**
     * Метод генерирует голоса для всех участников голосования.
     * Для тех участников, у которых заданный номер кандидата >= 0, генерируется значение голоса
     * Для тех, у которых заданный номер кандидата < 0, значение голоса остается стандартным,
     * то есть равно 1 (участник зарегестрировался, но не проголосовал)
     */
    public void votersRegistrationAndVotingValueGeneration(){
        for(int i = 0; i < voters.size(); i++){
            if(votingValues.get(i) < 0){
                continue;
            }
            voters.get(i).vote(votingValues.get(i));
        }
    }

    /**
     * Метод выполняет получение всеми участниками голосования
     * дополнительных данных для восстановления tallyValue, после того,
     * как один/несколько участников голосования не проголосовали
     *
     * @return recoveredResult
     */
    public BigInteger tallyValueRecovery(){
        BigInteger recoveredResult = BigInteger.ONE;
        for(Voter voter : voters){
            if(voter.getVotingValue().equals(BigInteger.ONE)){
                continue;
            }
            recoveredResult =
                    recoveredResult.multiply(voter.getAdditionalValueToRecoverTallyValue(failedVoters));
        }
        return recoveredResult.mod(CommonVariables.p);
    }

    public BigInteger votingModel(){
        votersRegistrationAndVotingValueGeneration();

        BigInteger tallyValue = votingSimulationWithoutSomebody();
        System.out.println("Невалидный tally Value:\t" + tallyValue);

        BigInteger recoveredTallyValue = tallyValueRecovery().multiply(tallyValue).mod(p);
        System.out.println("Восстановленный tally Value:\t" + recoveredTallyValue);

        return recoveredTallyValue;
    }
}
