package org.bindenko;

import java.util.ArrayList;
import java.math.BigInteger;

public class PerformNoVotesFailure {
    private final ArrayList<Voter> voters;
    private final ArrayList<Integer> votingValues;

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
            tallyValue = tallyValue.multiply(voter.getVotingValue());
        }
        return tallyValue;
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
}
