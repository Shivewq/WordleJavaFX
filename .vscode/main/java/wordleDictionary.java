import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
* readDictionary() reads through valid-wordle-words.txt file in /resources which contains a variety of 5-letter words.
* Uses File from java.io & fetches resource, searches with the Scanner.
*   @return returns a string list of words to be used as possible wordles for the game. 
*/ 
public class wordleDictionary {

    public List<String> readDictionary() {
        File file = new File(getClass().getResource("valid-wordle-words.txt").getPath());
        List<String> wordleList = new ArrayList<>();    
        Scanner scanner;
        try {
            scanner = new Scanner(file);
            String line = scanner.nextLine();
            while(scanner.hasNextLine()){
                wordleList.add(line);
                line = scanner.nextLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return wordleList;
    }

        /**
    *   getRandomWordle() method takes a String list as a parameter. This method reads through the list of words,  
    *   & picks a word at random within. 
    *   @return returns a single string word based on the position of word within the list.
    */ 
    public String getRandomWordle(List<String> wordleList){
        Random random = new Random();
        int position = random.nextInt(wordleList.size());
        return wordleList.get(position).toUpperCase();
    }
}

