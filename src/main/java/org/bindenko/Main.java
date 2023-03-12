package org.bindenko;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static String defaultPath = "src/main/resources/votingValues.txt";
    public static void main(String[] args) throws IOException {
        //чтение голосов из файла
        ArrayList <Integer> votingValues;
        if(args.length == 1) { // путь_до_файла_с_голосами
            votingValues = Util.readFile(args[0]);
        }else if (args.length == 2){ // колво_участников колво_кандидатов
            votingValues = Util.readFile(defaultPath);
        }else if (args.length == 3){ // колво_участников колво_кандидатов путь_до_файла_с_голосами
            votingValues = Util.readFile(defaultPath);
        } else if (args.length == 0) { // используется автоматическое определение количества участников и голосов и стандартный файл с голосами
            votingValues = Util.readFile(defaultPath);
        }else{
            throw new IOException("Неверный ввод");
        }

        //регистрация участников
        ArrayList<Voter> voters = new ArrayList<>();
        for (Integer votingValue : votingValues) {
            Voter voter = new Voter();
            voters.add(voter);
        }

        //голосование
        for(int i = 0; i < voters.size(); i++){
            voters.get(i).vote(votingValues.get(i));
        }

        //подсчет голосов каждым участником
        for (Voter voter : Voter.votersPubKeys.values()) {
            BigInteger tallyValue = voter.getValidTallyValue();
            System.out.println("Voter " + voter.getPubKey());
            System.out.println("recovered tallyValue:\t" + tallyValue);
            System.out.println("ballot distribution:\t" + Arrays.toString(voter.tally(tallyValue)));
            System.out.println();
        }
    }
}
