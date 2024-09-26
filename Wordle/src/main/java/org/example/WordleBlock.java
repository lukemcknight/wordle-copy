package org.example;

import javax.swing.*;

public class WordleBlock extends JButton {
    private int row;
    private int col;
    private boolean isTaken;
    private boolean isValid;
    public WordleBlock(int row, int col){
        super();
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isTaken() {
        return isTaken;
    }

    public void setTaken(boolean taken) {
        isTaken = taken;
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}
