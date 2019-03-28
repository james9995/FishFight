package com.mygdx.fishFightGame.sprites;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.fishFightGame.Game;

public class IceBlock {
    private Vector2 position;
    private int blockNumber, UH, UHp, DH, DHp,HZ, HZp, fish;
    private char penguin;

    public IceBlock(int i, int x, int y, int f) {
        blockNumber = i;
        penguin = 'n';
        UH = (int)(((i%15)%7) + 1 + 7*Math.floor((i%15)/14) + Math.floor(i/15));
        UHp = (int)(Math.floor(i/15)*2 + 1 + Math.floor((i%15+0.5)/7.5));
        DH = (UH + 5) - UHp;
        DHp = UHp;
        HZ = UHp;
        HZp = UH;
        position = new Vector2(x, y);
        fish = f;
    }

    public Vector2 getPosition() {
        return position;
    }

    public int getBlockNumber() {
        return blockNumber;
    }

    public int getDistance(float x, float y) {
        return (int) ( Math.pow((position.x + 46 - x),2) + Math.pow( (position.y + 54 -  (Game.HEIGHT - y)),2));
    }

    public int getFish() {
        return fish;
    }

    public int getScore() {
        if (fish > 0 && fish < 11) {return 3;}
        else if (fish <31) {return 2;}
        else {return 1;}
    }

    public void setFish(int n) {
        fish = n;
    }

    public char getPenguin() {
        return penguin;
    }

    public void setPenguin(char n) {
        penguin = n;
    }

    public int getRow(char c) {
        if (c == 'u') {return UH;}
        else if (c == 'd') {return DH;}
        else {return HZ;}
    }

    public int getPos(char c) {
        if (c == 'u') {return UHp;}
        else if (c == 'd') {return DHp;}
        else {return HZp;}
    }


}
