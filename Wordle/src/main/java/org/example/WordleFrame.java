package org.example;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONTokener;

public class WordleFrame extends JFrame implements KeyListener {
    private static final int ROW = 6;
    private static final int COL = 5;
    WordleBlock[][] blocks = new WordleBlock[ROW][COL];
    JPanel mainPnl;
    JPanel headerPnl;
    JPanel blockPnl;
    JLabel headerLbl;

    String[] letters = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
    String[] upperLetters = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

    private String[] parsedWord = new String[5];

    int rowsFilled = 0;
    int colsFilled = 0;

    Color grey = new Color(166, 163, 154);
    Color green = new Color(48, 145, 74);
    Color yellow = new Color(203, 209, 90);

    public WordleFrame(){
        setSize(500,600);

        mainPnl = new JPanel();
        mainPnl.setLayout(new BorderLayout());

        createHeaderPanel();
        mainPnl.add(headerPnl, BorderLayout.NORTH);

        createBlockPanel();
        mainPnl.add(blockPnl, BorderLayout.CENTER);

        this.addKeyListener(this);
        this.setFocusable(true);

        add(mainPnl);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void createHeaderPanel() {
        headerPnl = new JPanel();
        headerPnl.setLayout(new FlowLayout());
        headerPnl.setBackground(Color.WHITE);

        Font customFont = loadCustomFont("/Fonts/ARIALBLD.TTF",40);

        headerLbl = new JLabel("WORDLE");
        headerLbl.setFont(customFont);

        headerPnl.add(headerLbl);
    }

    private void createBlockPanel() {
        blockPnl = new JPanel();
        blockPnl.setLayout(new GridLayout(ROW, COL));
        blockPnl.setBackground(Color.WHITE);

        Font customFont = loadCustomFont("/Fonts/ARIALBLD.TTF",26);
        parseWord(getWord());

        if(customFont != null){
            for(int row = 0; row < ROW; row++){
                for(int col = 0; col < COL; col++){
                    blocks[row][col] = new WordleBlock(row, col);

                    LineBorder customBorder = new LineBorder(grey, 2);



                    JPanel buttonPanel = new JPanel();
                    buttonPanel.setLayout(new BorderLayout());
                    buttonPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
                    blocks[row][col].setBackground(Color.WHITE);
                    blocks[row][col].setBorder(customBorder);
                    blocks[row][col].setFont(customFont);
                    buttonPanel.setBackground(Color.WHITE);
                    buttonPanel.add(blocks[row][col], BorderLayout.CENTER);

                    blockPnl.add(buttonPanel);
                }
            }
        }
    }

    public void addLetter(String keyChar) {
        if (rowsFilled < ROW && colsFilled < COL) {
            if (!blocks[rowsFilled][colsFilled].isTaken()) {
                blocks[rowsFilled][colsFilled].setText(keyChar.toUpperCase());
                blocks[rowsFilled][colsFilled].setTaken(true);
                colsFilled++;
            }
        }
    }

    public void onEnter() {
        if (colsFilled == 5) {
            String jsonFilePath = "/json/words.json";
            JSONTokener token = new JSONTokener(WordleFrame.class.getResourceAsStream(jsonFilePath));
            JSONArray array = new JSONArray(token);
            String enteredWord = "";
            for(int z = 0; z < COL; z++){
                enteredWord += blocks[rowsFilled][z].getText().toLowerCase();
            }

            boolean wordFound = false;
            for(int i = 0; i < array.length(); i++){
                if(enteredWord.equals(array.getString(i))){
                    wordFound = true;
                    break;
                }
            }
            if(wordFound) {
                for (int i = 0; i < COL; i++) {
                    if (blocks[rowsFilled][i].getText().equals(parsedWord[i])) {
                        LineBorder greenBorder = new LineBorder(green, 2);
                        blocks[rowsFilled][i].setBackground(green);
                        blocks[rowsFilled][i].setBorder(greenBorder);
                        blocks[rowsFilled][i].setValid(true);
                    } else if (Arrays.asList(parsedWord).contains(blocks[rowsFilled][i].getText())) {
                        LineBorder yellowBorder = new LineBorder(yellow, 2);
                        blocks[rowsFilled][i].setBackground(yellow);
                        blocks[rowsFilled][i].setBorder(yellowBorder);
                    } else {
                        blocks[rowsFilled][i].setBackground(grey);
                    }
                    blocks[rowsFilled][i].setForeground(Color.WHITE);
                }
                colsFilled = 0;
                rowsFilled++;
                checkWin();
                checkLoss();
            } else {
                JOptionPane.showMessageDialog(null,"Word is not in list.");
            }
        }
    }

    public void onDelete(){
        if(colsFilled > 0){
            colsFilled--;
        }
        blocks[rowsFilled][colsFilled].setTaken(false);
        blocks[rowsFilled][colsFilled].setText("");
    }

    public void checkWin(){
        int validBlocks = 0;
        for(int i = 0; i < COL; i++){
            if(blocks[rowsFilled - 1][i].isValid()){
                validBlocks++;
            }
        }
        if(validBlocks >= 5){
            JOptionPane.showMessageDialog(null, "You won!");
            resetGame();
        }
    }

    public void checkLoss(){
        if(rowsFilled >= 6){
            JOptionPane.showMessageDialog(null,"You Lost!");
            resetGame();
        }
    }

    public void resetGame(){
        rowsFilled = 0;
        colsFilled = 0;
        LineBorder customBorder = new LineBorder(grey, 2);
        parseWord(getWord());
        for(int row = 0; row < ROW; row++){
            for(int col = 0; col < COL; col++){
                blocks[row][col].setValid(false);
                blocks[row][col].setTaken(false);
                blocks[row][col].setBackground(Color.WHITE);
                blocks[row][col].setBorder(customBorder);
                blocks[row][col].setForeground(Color.BLACK);
                blocks[row][col].setText("");
            }
        }
    }

    public static Font loadCustomFont(String fontFilePath, int fontSize){
        try{
            InputStream fontStream = WordleFrame.class.getResourceAsStream(fontFilePath);
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            return customFont.deriveFont(Font.PLAIN, fontSize);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String getWord() {
        String jsonFilePath = "/json/words.json";
        String word;
        Random rand = new Random();
        int randWord = rand.nextInt(4500);

        try {
            JSONTokener token = new JSONTokener(WordleFrame.class.getResourceAsStream(jsonFilePath));
            JSONArray array = new JSONArray(token);

            word = array.getString(randWord);
            return word;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void parseWord(String word){
        char[] characters = word.toCharArray();
        for(int i = 0; i < word.length(); i++){
            parsedWord[i] = String.valueOf(characters[i]).toUpperCase();
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {
        String keyChar = String.valueOf(e.getKeyChar());
        for(int i = 0; i < letters.length; i++){
            if(keyChar.equals(letters[i])){
                addLetter(keyChar);
            } else if (keyChar.equals(upperLetters[i])){
                addLetter(keyChar);
            }
        }
        if(e.getKeyChar()=='\n'){
            onEnter();
        }
        if(e.getKeyChar() == KeyEvent.VK_BACK_SPACE){
            onDelete();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
