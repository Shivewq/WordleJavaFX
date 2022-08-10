import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class PrimaryController {

    // get FXML UI content variables
    @FXML private GridPane gamePane;
    @FXML private ButtonBar buttonRow1;
    @FXML private ButtonBar buttonRow2;
    @FXML private Label wordleQue;          // to display notices for certain actions performed
    @FXML private Button ENTER;
    @FXML private Button DELETE;
    @FXML private CheckBox displayWordle;
    @FXML private Label currentWordle;
    // Letters 
    private String letter;
    private char[] characters = 
    {'Q','W','E','R','T','Y','U','I','O','P',
    'A','S','D','F','G','H','J','K','L',
    'Z','X','C','V','B','N','M'};
    
    // tracking GridPane's Rows, Columns & specific cells
    private int currentColumn = 0;
    private int currentRow = 0;

    private List<Button> buttonList;        // List of buttons for keyboard
    private List<String> letterList;        // Store as a list of input letters by user
    private List<String> correctLetters;

    private String guess;                                                   // User's final guess (word) for each row
    private String wordle;              // Wordle / Correct word
    
    Timer timer = new java.util.Timer();
    wordleDictionary dictionary = new wordleDictionary();

    @FXML
    private void helpScreen() throws IOException {
        App.setRoot("wordleMenu");
    }
    
    /**
    * initialize() is used to set up the button (mainly keyboard) controls for the user to use & input letters A-Z
    * Iterates twice through letters for 2 JavaFX UI Button bars, which store several buttons. 
    * Buttons & their letters are also stored in a list for to track the user's specific inputs later. 
    * also sets an Action event to each button added, calling the addCharacter() method as long as currentColumn is less than 5.
    *  @return Assortment of keys identical to QWERTY set up
    */
    @FXML
    public void initialize() {
        buttonList = new ArrayList<>();                             // Set arrayList for buttons
        letterList = new ArrayList<>();                             // Set arrayList for clicked letters
        wordle = dictionary.getRandomWordle(dictionary.readDictionary());
        currentWordle.setText(wordle);
        currentWordle.setVisible(false);
        // Button row 1: QWERTYUIOPASD
        for (int i = 0; i < 13; i++) {
            Button button = new Button(Character.toString(characters[i]));
            // configure button
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent event) {

                    letter = button.getText();                      // assign letter to the button's text                                           
                    if(currentColumn < 5){                          // User may only add a letter until final cell           
                        addCharacter();                             // Call method to add letters to grid content
                    }                                       
                    wordleQue.setVisible(false);
                }
            });
            buttonList.add(button);
            buttonRow1.getButtons().add(button);  
        }
        // Button row 2: FGHJKLZXCVBNM
        for (int i = 13; i < 26; i++) {                         
            Button button = new Button(Character.toString(characters[i]));
            // configure button
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent event) {

                    letter = button.getText();                         
                    if(currentColumn < 5){
                        
                        addCharacter();  
                    }                       
                    wordleQue.setVisible(false);
                }
            });
            buttonList.add(button);
            buttonRow2.getButtons().add(button);  
        }
    }

    /**
    *   validUserWord() Also takes the String List as a Parameter.  
    *   this Method searches the passed list & checks if the user's guess (of 5 letters) is a real word.
    *   **Please note that this logic is only based on the scope of the list's content, and not every word exists in the list.
    *   @return true if the guess exists as a real word, false if not.
    */ 
    private boolean validUserWord(List<String> wordleList) {
        if (wordleList.contains(guess.toLowerCase())){          //toLowerCase since dictionary is lower cased
            return true;
        }
        return false;
    }

    /**
    *   The check() method takes an Action Event as a parameter (from the FXML)
    *   checks if displayWordle (id) checkbox is on or off
    *   *This is mainly for making it easier to test the game.
    *   @return true if checkbox is ticked & displays the Wordle. False if unticked, does not show wordle.
    */ 
    @FXML
    private void check(ActionEvent event) {
        if(displayWordle.isSelected()) {
            currentWordle.setVisible(true);
        }
        else {
            currentWordle.setVisible(false);
        }
    }

    /**
    *   getNodeByRowColumnIndex() takes integers *row*, *column*, & *GridPane* as a parameter.
    *   This method is key to fetch specific Label nodes the User adds/deletes by row & column in the grid.
    *   Uses an observable list to store the @FXML gamePane children. 
    *   @return results of the node/label's position according to row, column.
    */ 
    private Label getNodeByRowColumnIndex(final int row, final int column, GridPane gamePane) {
        ObservableList<Node> childrens = gamePane.getChildren();
        Label result = (Label) childrens.get((row*5)+column);
        return result;
    }

    /**
    *   incrementColumn() is used to simply add 1 to the currentColumn of the grid. 
    *   if Statement to make sure it doesn't exceed the grid.
    *   @return incremented column by 1 (increase currentColumn value)
    */ 
    private int incrementColumn(){   
        return currentColumn++;
    }

    /**
    *   removeColumn() is used to simply subtract 1 to the currentColumn of the grid. 
    *   if Statement to make sure it doesn't exceed the grid.
    *   @return remove 1 from currentColumn as long as it's more than 0 cells, otherwise do not reduce.
    */ 
    private int removeColumn(){
        if(currentColumn > 0){      
            return currentColumn--;
        }
        return currentColumn;
    }

    /**
    *   incrementRow() is used to simply add 1 to the currentRow of the grid. 
    *   if Statement to make sure it doesn't exceed the grid.
    *   @return adds 1 increment to Row as long as it's less than 6 cells from max, otherwise do not increment.
    */ 
    private int incrementRow(){
        if(currentRow < 5){         
            return currentRow++;
        }
        return currentRow;
    }

    /**
    *   addCharacter() adds labels containing the letter (fetched from initalize() Action Event) 
    *   The label is modified to be aligned, & spaced for highlighting later. 
    *   the Grid increments the column to move on to the next cell. 
    *   @return Label nodes that are added based on the current row & column the GridPane is on.
    */  
    private void addCharacter() {
        letterList.add(letter);
        Label text = new Label(letter);
        text.setAlignment(Pos.CENTER);
        text.setPrefWidth(58);
        text.setPrefHeight(54);
        gamePane.add(text, currentColumn, currentRow);
        incrementColumn();
    }

    /**
    *   deleteCharacter() method is for the DELETE button in my FXML UI.  
    *   uses the my getNode...() method to delete the Label the user would like to delete (based on Row & column).
    *   this also removed the most recent letter in the letterList (as it's the added character), & goes back a column.
    */  
    @FXML
    private void deleteCharacter() throws IOException {
        if(currentColumn > 0){
            gamePane.getChildren().remove(getNodeByRowColumnIndex(currentRow, currentColumn, gamePane));    //remove the current Tile of the GridPane
            letterList.remove(letterList.size() - 1);      // remove letter from list 
            removeColumn();
        } else {
            wordleQue.setText("Cannot delete anymore letters");
            wordleQue.setVisible(true);
        }
    }

    /**
    *   joinLetters() method briefly joins letters contained in the list of Letters
    *   This works as the List of letters is only letters that exist on the grid at any time. 
    *   this is applied to a string variable "guess" to be used in another method. 
    */   
    private void joinLetters() {
        guess = String.join("", letterList);
    }
    
    /**
    *   submitGuess() Method is called when the ENTER button is pressed. 
    *   This method only proceeds if the user has successfully input 5 letters AND is a real word (based on used dictionary)
    *   If not, it will que the application to display a message stating it is not a valid 5 letter word. 
    *   this method will also trigger a Game Over message once the final submission has been made (last row, last column.)
    *   @return checkGuess method, increments Row, Clears list of letters to be used for the next row.
    */  
    @FXML //When user clicks ENTER to submit a guess 
    private void submitGuess() throws IOException { 
        joinLetters();                                         
        if(currentRow < 5) {
            if ((currentColumn == 5) && (validUserWord(dictionary.readDictionary()))){      
                checkGuess();                                                
                incrementRow();                                            
                letterList.clear();                                         
                currentColumn = 0;
            }
            else { 
                wordleQue.setText("Please enter a valid 5-letter word.");
                wordleQue.setVisible(true);  
            }    
        } else {
            checkGuess();                         
            gameOver();                                      
        }
    }

    /**
    *   checkGuess() method calls on one of two other methods,
    *   based on if the guess String variable relation to the chosen wordle.
    *   @return call highlightWordle() method if guess is correct. call highlightCharacters() if not.
    */  
    private boolean checkGuess(){
        if (guess.equals(wordle)){                             
            highlightWordle();                                                
            return true;
        } else {
            highlightCharacters(getWordleChars());
        }
        return false;
    }

    /**
    *   getWordleChars() method fetches each letter of the Wordle.
    *   Done by splitting the wordle & applying them to a String List.
    *   @return a list of characters that come from the wordle.
    */ 
    private List<String> getWordleChars(){
        List<String> wordleChars = new ArrayList<>();
        wordleChars = Arrays.asList(wordle.split(""));
        return wordleChars;
    }
    
    /**
    *   highlightCharacters() takes a String List as a parameter, which is fetched by the getWordleChars() method.  
    *   This iterates through the grid (Column-oriented), & is used to get the text of those columns using the
    *   getNodeByRowColumnIndex method. 
    *    @return
    *   if the the collected input contains letters from the wordle, proceed, if not, it returns a grayed out cell.
    *   if the input's characters are in the same exact spot of the wordle's characters, return a green cell.
    *   ^ This also calls the highlightButton method below. 
    *   if the input's characters exist, but not in the correct area, return an orange cell. 
    */ 
    private void highlightCharacters(List<String> wordleChars) {
        correctLetters = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            Label input = getNodeByRowColumnIndex(currentRow, i, gamePane);
            if (wordleChars.contains(input.getText())) {                                                                       
                if (wordleChars.get(i - 1).equals(input.getText())) {        //subtract 1 as String index starts at 0                                                       
                    input.setStyle("-fx-background-color: green");
                    correctLetters.add(input.getText());
                    highlightButtons();       
                } else { input.setStyle("-fx-background-color: orange"); } 
            } else { input.setStyle("-fx-background-color: gray"); }      
        }
    }

    /**  
    *   highlightButtons() is an extra method iterates through all buttons & fetches it's character to be highlighted. 
    *   @return
    *   highlights button if the character exists & in the right position.
    */ 
    private void highlightButtons() {
        for (int i = 0; i < 26; i++) {
            if (correctLetters.contains(buttonList.get(i).getText())) {                                                                                                                                     
                buttonList.get(i).setStyle("-fx-background-color: green");        
            }   
        }      
    }

    /**
    *   highlightWordle() method highlights the words in all green if it's correct.
    *   iterate through & fetch cells using getNodeByRowColumnIndex().
    *   @return highlighted green cells & an updated message.
    */ 
    private void highlightWordle() {
        correctLetters = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            getNodeByRowColumnIndex(currentRow, i, gamePane).setStyle("-fx-background-color: green");  
            wordleQue.setText("Excellent!"); 
            wordleQue.setVisible(true);
            delayTimer();
        }
    }

    /**
    *   gameOver() is a brief method that displays a message when you reach the end of the grid.
    */  
    private void gameOver() {
        wordleQue.setText("Game Over!");
        wordleQue.setVisible(true);
        delayTimer();
    }

    /**
    *   delayTimer() method switches back to the main menu a little while the suer completes the game. 
    *   the Timer class helps set a delay before an action is performed.
    *   using TimerTask class, certain tasks can be scheduled for one-time or repeated execution by a Timer. 
    */ 
    private void delayTimer() {
        timer.schedule(new TimerTask() {
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run() {
                        try {
                            App.setRoot("wordleMenu");
                            timer.cancel();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, 2200, 20);
    }
}