package org.bindenko;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;

public class Util {

    public static ArrayList<Integer> readFile(String filePath){
        ArrayList<Integer> votingValues = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine();
            while (line != null) {
                votingValues.add(Integer.valueOf(line));
                line = reader.readLine();
            }
            System.out.println("Голоса участников:\t" + votingValues);
        } catch (Exception e) {
            e.printStackTrace();
        }

        CommonVariables.initializePrimeNumbers(Collections.max(votingValues) + 1);
        System.out.println("Простые числа, соответствующие номерам кандидатов\t" + CommonVariables.primeNumbers);
        System.out.println("\n");
        return votingValues;
    }
}
