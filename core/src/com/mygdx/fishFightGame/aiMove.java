package com.mygdx.fishFightGame;

public class aiMove {
    private int startBlock, endBlock;
    public aiMove(int i, int j){
        startBlock = i;
        endBlock = j;
    }

    public int getStartBlock() {
        return startBlock;
    }

    public int getEndBlock() {
        return endBlock;
    }
}
