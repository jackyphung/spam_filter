package assignment1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.*;

public class Trainer{

    private Map<String, Integer> trainHamFreq;
    private Map<String, Integer> trainSpamFreq;
    private Map<String, Double> probabilitySpamMap;
    private Map<String, Double> probabilityHamMap;
    private Map<String, Double> probabilityOfSpamMap;
    private ArrayList<String> allWords;
    private int spamFileCount = 0;
    private int hamFileCount = 0;
    private int testFileCount = 0;
    private double numTruePositives = 0;
    private double numTrueNegatives = 0;
    private double numFalseNegatives = 0;

    public static double accuracy = 0;
    public static double precision = 0;
    public static ObservableList<TestFile> fileData = FXCollections.observableArrayList();

    public Trainer(){
        trainHamFreq = new TreeMap<>();
        trainSpamFreq = new TreeMap<>();
        allWords = new ArrayList<>();
        probabilitySpamMap = new TreeMap<>();
        probabilityHamMap = new TreeMap<>();
        probabilityOfSpamMap = new TreeMap<>();
    }

    //trainer
    public void parseFile(File file) throws IOException{
        if(file.isDirectory()){
            System.out.println(file.getName());
            File[] content = file.listFiles();
            for(File current: content){
                parseFile(current);
            }
        }
        //train spam folder
        else if(file.getParentFile().getName().equals("spam")){
            Scanner scanner = new Scanner(file);
            //count spam files
            spamFileCount += 1;
            //temporary array to store words to check if it has been counted.
            ArrayList<String> tempWords = new ArrayList<>();
            while (scanner.hasNext()){
                String token = scanner.next();
                if (isValidWord(token)){
                    //checks the tempWords array if the word is not in it then add it and count it once per file.
                    if(!tempWords.contains(token)){
                        if(!allWords.contains(token)){
                            allWords.add(token);

                        }
                        tempWords.add(token);
                        countSpamWord(token);
                    }
                }
            }
            scanner.close();
        }
        //train ham folder
        else{
            Scanner scanner = new Scanner(file);
            //count ham files
            hamFileCount += 1;
            //temporary array to store words to check if it has been counted.
            ArrayList<String> tempWords = new ArrayList<>();
            while (scanner.hasNext()){
                String  token = scanner.next();
                if (isValidWord(token)){
                    //checks the tempWords array if the word is not in it then add it and count it once per file.
                    if(!tempWords.contains(token)){
                        if(!allWords.contains(token)){
                            allWords.add(token);
                        }
                        tempWords.add(token);
                        countWord(token);
                    }
                }
            }
            scanner.close();
        }
    }

    //process the test folder and testing it with training results
    public void parseTestFile(File file) throws IOException{
        if(file.isDirectory()){
            System.out.println(file.getName());
            File[] content = file.listFiles();
            for(File current: content){
                parseTestFile(current);
            }
        }
        else{
            Scanner scanner = new Scanner(file);
            //count test files
            testFileCount += 1;
            String fileName = file.getName();
            String actualClass = file.getParentFile().getName();
            Double sumOfProb = 0.0;
            while (scanner.hasNext()){
                String token = scanner.next();
                if (isValidWord(token)){
                    //checks if the word in the test file exist in the spam probability map and runs the calculation to determine it's probability
                    if(probabilityOfSpamMap.containsKey(token)){
                        if(!probabilityOfSpamMap.get(token).equals(0.0) && !probabilityOfSpamMap.get(token).equals(1.0)){
                            sumOfProb += (Math.log(1-probabilityOfSpamMap.get(token))-Math.log(probabilityOfSpamMap.get(token)));
                        }
                    }
                    else{
                        sumOfProb +=0;
                    }
                }
            }
            scanner.close();
            Double isSpam = 1 / (1+Math.pow(Math.E,sumOfProb));

            //checks how many files are accurate
            if(actualClass.equals("spam") && isSpam > 0.5){
                numTruePositives += 1;
            }
            else if (actualClass.equals("ham") && isSpam > 0.5){
                numFalseNegatives += 1;
            }
            else if (actualClass.equals("ham") && isSpam < 0.5){
                numTrueNegatives += 1;
            }

            //adds object to the the observable list for output
            fileData.add(new TestFile(fileName, isSpam, actualClass));
        }
    }

    //calculates the probability of it being a spam based on the frequency of the word appearing in spam files
    public void probabilitySpamCalculator(){
        for(Map.Entry<String, Integer> entry: trainSpamFreq.entrySet()){
            String word = entry.getKey();
            Integer value = entry.getValue();
            Double probability = (double)value/spamFileCount;

            probabilitySpamMap.put(word,probability);
        }
    }

    //calculates the probability of it being a ham based on the frequency of the word appearing in ham files
    public void probabilityHamCalculator(){
        for(Map.Entry<String, Integer> entry: trainHamFreq.entrySet()){
            String word = entry.getKey();
            Integer value = entry.getValue();
            Double probability = (double)value/hamFileCount;

            probabilityHamMap.put(word,probability);
        }
    }

    //calculates the probability of the word being a spam, then placed into a TreeMap.
    public void probabilityOfSpamCalculator(){
        for(int i = 0; i < allWords.size(); i++){
            String word = allWords.get(i);
            Double probability = 0.0;
            if(probabilitySpamMap.containsKey(allWords.get(i)) && probabilityHamMap.containsKey(allWords.get(i))){
                probability = probabilitySpamMap.get(allWords.get(i))/(probabilitySpamMap.get(allWords.get(i)) + probabilityHamMap.get(allWords.get(i)));
                probabilityOfSpamMap.put(word,probability);
            }
            else if(probabilitySpamMap.containsKey(allWords.get(i)) && !probabilityHamMap.containsKey(allWords.get(i))){
                probability = 1.0;
                probabilityOfSpamMap.put(word,probability);
            }
        }
    }

    //calculates the accuracy of result
    public void accuracyCalculator(){
        accuracy = (numTruePositives+ numTrueNegatives)/ testFileCount;
    }

    //calculates the precision of result
    public void precisionCalculator(){
       precision = numTruePositives / (numFalseNegatives+numTruePositives);
    }

    //determines if the word is an actual word
    private boolean isValidWord(String word){
        String allLetters = "^[a-zA-Z]+$";
        // returns true if the word is composed by only letters otherwise returns false;
        return word.matches(allLetters);

    }

    //Counts frequency of word
    private void countWord(String word){
        if(trainHamFreq.containsKey(word)){
            int previous = trainHamFreq.get(word);
            trainHamFreq.put(word, previous+1);
        }else{
            trainHamFreq.put(word, 1);
        }
    }

    //Counts frequency of spam word
    private void countSpamWord(String word){
        if(trainSpamFreq.containsKey(word)){
            int previous = trainSpamFreq.get(word);
            trainSpamFreq.put(word, previous+1);
        }else{
            trainSpamFreq.put(word, 1);
        }
    }

    //system output of results to see if values are somewhat accurate
    public void debugMessages(){
        System.out.println("Total ham words:" + trainHamFreq.keySet().size());
        System.out.println("Total spam words:" + trainSpamFreq.keySet().size());
        System.out.println("Total spam files:" + spamFileCount);
        System.out.println("Total ham files:" + hamFileCount);
        System.out.println("Total words:" + allWords.size());
        System.out.println("Total spam probability word:" + probabilityOfSpamMap.keySet().size());
        System.out.println("Total num true positives:" + numTruePositives);
        System.out.println("Total num false negatives:" + numFalseNegatives);
        System.out.println("total true negatives:" + numTrueNegatives);
        System.out.println("total test file:" + testFileCount);
        System.out.println("accuracy:" + accuracy);
        System.out.println("precision:" + precision);
    }
}