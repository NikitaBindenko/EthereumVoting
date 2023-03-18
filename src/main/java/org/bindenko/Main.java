package org.bindenko;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Есть возможность задать количество кандидатов и файл с голосами участников
 * Количество участников и их голоса определяются файлом votingValues.txt
 * (либо файлом, переданным в качестве аргумента). Количество участников равно
 * количеству строк в файле votingValues.txt. В каждой строке указывается номер кандидата,
 * за которого голосует участник (число от 0 до N-1, где N - количество кандидатов).
 * Пример валидного файла votingValues.txt лежит в репозитории проекта.
 *
 * Для того, чтобы имитировать отсутствие голоса от кандидата, в соответствующей строке
 * указывается отрицательное число (например, -1).
 *
 * После запуска программа выводит голоса участников, и список простых чисел,
 * соответствующих номерам кандидатов. В случае, если при запуске программы в аргументе
 * не было указано количество кандидатов, оно автоматически определяется максимальным номером
 * кандидата, указанном в файле votingValues.txt.
 *
 * Также выводится информационное сообщение, указывающее на то,
 * какие аргументы командной строки были переданы
 *
 * Резульататом работы программы является список участников голосования, для каждого из которых указаны
 * значение голоса, которое участник отправил остальным участникам голосования и результат голосования
 * и наглядное представление распределения голосов среди кандидатов в виде массива, где номеру элемента
 * массива соответствует число голосов, которое набрал участник с этим номером.
 * Идентификатором участника голосования служит публичный ключ участника (как в сети Ethereum)
 *
 */
public class Main {
    public static String defaultPath = "src/main/resources/votingValues.txt";
    public static void main(String[] args) throws IOException {
        //чтение голосов из файла
        ArrayList <Integer> votingValues;
        if(args.length == 1) {
            try {                                                           //кол-во кандидатов
                Integer numberOfCandidates = Integer.parseInt(args[0]);
                votingValues = Util.readFile(numberOfCandidates, defaultPath);
                System.out.println("Задано количество кандидатов (" + args[0] + ")");
            }catch (NumberFormatException e){                               //путь_до_файла_с_голосами
                votingValues = Util.readFile(null, args[0]);
                System.out.println("Задан путь до файла (" + args[0] + ")");
            }
        } else if (args.length == 2){ //колво_кандидатов путь_до_файла_с_голосами
            votingValues = Util.readFile(Integer.parseInt(args[0]), args[1]);
            System.out.println("Задано количество кандидатов (" + args[0] + ") и путь до файла (" + args[1] + ")");
        } else if (args.length == 0) { // используется автоматическое определение количества участников и голосов и стандартный файл с голосами
            votingValues = Util.readFile(null, defaultPath);
            System.out.println("Используется стандартный файл с голосами (votingValues.txt)" +
                    "\nКоличество кандидатов определено автоматически на основе голосов участников");
        }else{
            System.out.println("Неверный ввод");
            System.exit(0);
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
            System.out.println("\nVoter " + voter.getPubKey());
            System.out.println("Voting value:\t" + voter.getVotingValue());
            System.out.println("recovered tallyValue:\t" + tallyValue);
            System.out.println("ballot distribution:\t" + Arrays.toString(voter.tally(tallyValue)));
        }
    }
}
