package org.bindenko;

import java.math.BigInteger;
import java.util.ArrayList;

import static org.bindenko.CommonVariables.p;
import static org.bindenko.Voter.failedVoters;

public class Main {
    public static String defaultPath = "src/main/resources/votingValues.txt";
    public static void main(String[] args) {
        ArrayList <Integer> votingValues;
        if(args.length == 3) {
            votingValues = Util.readFile(args[2]);
        }else{
            votingValues = Util.readFile(defaultPath);
        }

        for (Integer votingValue : votingValues) {
            Voter voter = new Voter();
            voter.vote(votingValue);
        }


        for (Voter voter : Voter.votersPubKeys.values()) {
            System.out.println(voter.getValidTallyValue());
            System.out.println("\n");
        }

        System.out.println(failedVoters);
    }
}
