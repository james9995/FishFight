package com.mygdx.fishFightGame.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.fishFightGame.Game;
import com.mygdx.fishFightGame.aiMove;
import com.mygdx.fishFightGame.sprites.IceBlock;

import java.util.Arrays;

public class PlayState extends State {
    private Texture background, iceBlockTexture, oneFishTexture, twoFishTexture, threeFishTexture,
            deadPenguinTexture, deadPenguinBig, redPenguinTexture, bluePenguinTexture, greenPenguinTexture,
            yellowPenguinTexture, highlightTexture,redPenguinBig, bluePenguinBig,
            greenPenguinBig, yellowPenguinBig, quitTexture, autoTexture, squareTexture,
            currentPlayerTexture, scoreRegion, winScreen, rematchTexture, crossTexture, musicTexture, speakerTexture;
    private Array<IceBlock> iceBlocks, iceBlocksBackup;
    private Array<aiMove> legalMoves;
    private long endPauseTime = 0;
    private char[] penguinChars;
    private char movementDirection;
    private int selectedBlock, lastSelectedBlock, lastValidSelectedBlock, highlightedBlock,
            remainingPenguins, turn, xInput, yInput, maxScore, autoPenguin;
    private boolean directionFlag, legalFlag, endFlag, drawFlag, aiTestFlag;
    private final static Vector2 PENGUIN_OFFSET = new Vector2(20,18);
    private final static Vector2 PENGUIN_SIZE = new Vector2(58,74);

    public int[] scores, autoFlags, aiMovePath;

    public PlayState(GameStateManager gsm) {
        super(gsm);
        cam.setToOrtho(false, Game.WIDTH,Game.HEIGHT);

        background = new Texture("background2.png");
        iceBlockTexture = new Texture("iceBlock.png");
        threeFishTexture = new Texture("threeFish.png");
        twoFishTexture = new Texture("twoFish.png");
        oneFishTexture = new Texture("oneFish.png");
        redPenguinTexture = new Texture("redPenguinS.png");
        bluePenguinTexture = new Texture("bluePenguinS.png");
        greenPenguinTexture = new Texture("greenPenguinS.png");
        yellowPenguinTexture = new Texture("yellowPenguinS.png");
        redPenguinBig = new Texture("redPenguin.png");
        bluePenguinBig = new Texture("bluePenguin.png");
        greenPenguinBig = new Texture("greenPenguin.png");
        yellowPenguinBig = new Texture("yellowPenguin.png");
        deadPenguinTexture = new Texture("deadPenguinS.png");
        deadPenguinBig = new Texture("deadPenguin.png");
        highlightTexture = new Texture("highlight.png");
        quitTexture = new Texture("quit.png");
        autoTexture = new Texture("auto.png");
        squareTexture = new Texture("square.png");
        currentPlayerTexture = new Texture("currentPlayer.png");
        scoreRegion = new Texture("scoreSprites.png");
        winScreen = new Texture("winScreen.png");
        rematchTexture = new Texture("rematch.png");
        crossTexture = new Texture("cross.png");
        musicTexture = new Texture("music.png");
        speakerTexture = new Texture("speaker.png");

        selectedBlock = -1;
        highlightedBlock = -1;
        lastSelectedBlock = -1;
        xInput = 0;
        yInput = 0;
        lastValidSelectedBlock = -1;
        maxScore = 0;
        movementDirection = 'z';
        directionFlag = false;
        legalFlag = true;
        endFlag = false;
        aiTestFlag = false;
        drawFlag = true;
        scores = new int[4];
        for (int i=0;i<4;i++) {scores[i] = 0;}
        autoFlags = new int[4];
        for (int i=0;i<4;i++) {autoFlags[i] = 0;}
        if (Game.PLAYERS == 3) {remainingPenguins = 9;} else {remainingPenguins = 8;}
        turn = (int) (Math.random()*(Game.PLAYERS-1) + 0.5);
        penguinChars = new char[4];
        penguinChars[0] = 'r'; penguinChars[1] = 'b'; penguinChars[2] = 'g'; penguinChars[3] = 'y';

        double[] fishGenerator = new double[60];
        double[] fishGeneratorSorted = new double[60];
        double[] fishGeneratorOrder = new double[60];
        for (int i = 0; i<60; i++) {fishGenerator[i] = Math.random(); fishGeneratorSorted[i] = fishGenerator[i];}
        Arrays.sort(fishGeneratorSorted);
        for (int i = 0; i<60; i++) {
            for (int j = 0; j<60; j++) {
                if (fishGenerator[j] == fishGeneratorSorted[i]) {fishGeneratorOrder[i] = j;}
            }
        }

        iceBlocks = new Array<IceBlock>();
               for(int i=0; i<60; i++) {
                   iceBlocks.add(new IceBlock(i, Math.round(320 + 91*(i%15) - (int) (Math.floor(((i%15)+0.5)/7.5))*683),591 - (int) (Math.floor(((i%15)+0.5)/7.5))*81 - (int) (Math.floor((i/15))*162), (int) (fishGeneratorOrder[i]+1)));
               }
    }

// Get number of adjacent valid blocks (for auto move)
    private int getDensity (int n) {
        int density = 0;
        for (int i = 0; i < 60; i++) {
            if (i != n && Math.sqrt(Math.pow(iceBlocks.get(i).getPosition().x - iceBlocks.get(n).getPosition().x,2)+Math.pow(iceBlocks.get(i).getPosition().y - iceBlocks.get(n).getPosition().y,2)) < 93.2 && iceBlocks.get(i).getFish() > 0) {density = density + 1;}
        }
        return density;
    }

    private int getPlayersTotalDensity() {
        int penguinCount = 0, density = 0;
        for (int i = 0; i < 60; i++) {
            if (iceBlocks.get(i).getPenguin() == penguinChars[turn]) {penguinCount = penguinCount + 1; density = density + getDensity(i);}
        }
        return  density;
    }

    private int getOtherPlayersTotalDensity() {
        int penguinCount = 0, density = 0;
        for (int i = 0; i < 60; i++) {
            if (iceBlocks.get(i).getPenguin() != penguinChars[turn] && iceBlocks.get(i).getPenguin() != 'd' && iceBlocks.get(i).getPenguin() != 'n') {penguinCount = penguinCount + 1; density = density + getDensity(i);}
        }
        return  density;
    }

    private int getPlayersPenguinCount() {
        int penguinCount = 0;
        for (int i = 0; i < 60; i++) {
            if (iceBlocks.get(i).getPenguin() == penguinChars[turn]) {penguinCount = penguinCount + 1;}
        }
        return penguinCount;
    }

    private int getOtherPlayersPenguinCount() {
        int penguinCount = 0;
        for (int i = 0; i < 60; i++) {
            if (iceBlocks.get(i).getPenguin() != penguinChars[turn] && iceBlocks.get(i).getPenguin() != 'd' && iceBlocks.get(i).getPenguin() != 'n') {penguinCount = penguinCount + 1;}
        }
        return penguinCount;
    }

    private int closestFishNum(int blockNumber, int fishNum, char axis, boolean direction) {
        int minDistance = 50;
        for (int i = 0; i < 60; i++) {
            if (iceBlocks.get(i).getFish() == fishNum &&
                    i != blockNumber &&
                    (iceBlocks.get(i).getPenguin() != penguinChars[turn] || (Math.sqrt(Math.pow(iceBlocks.get(i).getPosition().x - iceBlocks.get(blockNumber).getPosition().x,2)+Math.pow(iceBlocks.get(i).getPosition().y - iceBlocks.get(blockNumber).getPosition().y,2)) > 93.2)) &&
                    iceBlocks.get(i).getRow(axis) == iceBlocks.get(blockNumber).getRow(axis) &&
                    ((iceBlocks.get(i).getPos(axis) > iceBlocks.get(blockNumber).getPos(axis)) == !direction) &&
                    Math.abs((iceBlocks.get(i).getPos(axis) - iceBlocks.get(blockNumber).getPos(axis))) < minDistance)
            {minDistance = Math.abs((iceBlocks.get(i).getPos(axis) - iceBlocks.get(blockNumber).getPos(axis)));}
        }
        return minDistance;
    }

    private boolean isPenguinAlone(int blockNumber) {
        boolean alone = false;
        //ie if nearest blank is closer than nearest penguin in all directions
        if (closestFishNum(blockNumber,0,'h',true) >= closestFishNum(blockNumber,-1,'h',true) &&
        closestFishNum(blockNumber,0,'h',false) >= closestFishNum(blockNumber,-1,'h',false) &&
        closestFishNum(blockNumber,0,'d',true) >= closestFishNum(blockNumber,-1,'d',true) &&
        closestFishNum(blockNumber,0,'d',false) >= closestFishNum(blockNumber,-1,'d',false) &&
        closestFishNum(blockNumber,0,'u',true) >= closestFishNum(blockNumber,-1,'u',true) &&
        closestFishNum(blockNumber,0,'u',false) >= closestFishNum(blockNumber,-1,'u',false)) {alone = true;}
        return alone;
    }

    private boolean isPenguinReallyAlone(int blockNumber) {
        boolean reallyAlone = isPenguinAlone(blockNumber);
        for (int i = 0; i < 60; i++) {
            if (reallyAlone == true && i != blockNumber && iceBlocks.get(i).getFish() > 0 && (Math.sqrt(Math.pow(iceBlocks.get(i).getPosition().x - iceBlocks.get(blockNumber).getPosition().x,2)+Math.pow(iceBlocks.get(i).getPosition().y - iceBlocks.get(blockNumber).getPosition().y,2)) < 93.2) && !(isPenguinAlone(i)))
            {reallyAlone = false;}
        }
        return reallyAlone;
    }

    private boolean playersPenguinsAllReallyAlone() {
        boolean allAlone = true;
        for (int i = 0; i < 60; i++) {
            if (iceBlocks.get(i).getPenguin() == penguinChars[turn] && !isPenguinReallyAlone(i)) {allAlone = false;}
        }
        return allAlone;
    }


// Select any remaining penguin for player (for auto move)
    private int selectRemainingPenguin () {
        autoPenguin = -1;
        for (int i = 0; i < 60; i++) {
            if (iceBlocks.get(i).getPenguin() == penguinChars[turn]) {autoPenguin = i;}
        }
        return autoPenguin;
    }

    private Array<aiMove> getLegalMoves() {
        legalMoves = new Array<aiMove>(3600);
        for (int i = 0; i < 60; i++) {
            if (iceBlocks.get(i).getPenguin() == penguinChars[turn]) {
                for (int j = 0; j < 60; j++) {
                    //determine direction of proposed move
                    if (iceBlocks.get(i).getRow('u') == iceBlocks.get(j).getRow('u'))  {movementDirection = 'u';}
                    else if (iceBlocks.get(i).getRow('d') == iceBlocks.get(j).getRow('d'))  {movementDirection = 'd';}
                    else if (iceBlocks.get(i).getRow('h') == iceBlocks.get(j).getRow('h'))  {movementDirection = 'h';}
                    else {movementDirection = 'z';}
                    //determine valance of proposed move
                    if (iceBlocks.get(i).getPos(movementDirection) > iceBlocks.get(j).getPos(movementDirection)) {directionFlag = false;}
                    else {directionFlag = true;}
                    // determine if legal
                    legalFlag = true;
                    for (IceBlock block : iceBlocks) {
                        if (movementDirection == 'z'
                                || block.getBlockNumber() != i
                                && block.getRow(movementDirection) == iceBlocks.get(j).getRow(movementDirection)
                                && ((block.getPos(movementDirection) > iceBlocks.get(i).getPos(movementDirection)) == directionFlag)
                                && ((block.getPos(movementDirection) < iceBlocks.get(j).getPos(movementDirection)) == directionFlag )
                                && block.getFish() < 1)
                        {legalFlag = false;}
                    }
                    //if legal, add to array
                    if (legalFlag && j != i && iceBlocks.get(j).getFish() > 0) {legalMoves.add(new aiMove(i,j));}
                }

            }
        }
        return legalMoves;
    }

    private int[] getAiMovePath() {
        selectedBlock = -1;
        highlightedBlock = 0;
        double maxRand = -50;
        int selected = 0;
        int penguinCountPrior = getPlayersPenguinCount();
        int otherPenguinCountPrior = getOtherPlayersPenguinCount();
        int playersAverageDensityPrior = getPlayersTotalDensity();
        int otherPlayersAverageDensityPrior = getOtherPlayersTotalDensity();
        double[] priorityList;
        Array<aiMove> legalMoves = getLegalMoves();
        priorityList = new double[legalMoves.size];
        aiTestFlag = true;
        iceBlocksBackup = new Array<IceBlock>();
        for(int i=0; i<60; i++) {
            iceBlocksBackup.add(new IceBlock(i, (int) iceBlocks.get(i).getPosition().x, (int) iceBlocks.get(i).getPosition().y, iceBlocks.get(i).getFish()));
            iceBlocksBackup.get(i).setPenguin(iceBlocks.get(i).getPenguin());
        }
        for (int i = 0; i < legalMoves.size ; i++) {

            // reset
            iceBlocks = new Array<IceBlock>();
            for(int n=0; n<60; n++) {
                iceBlocks.add(new IceBlock(n, (int) iceBlocksBackup.get(n).getPosition().x, (int) iceBlocksBackup.get(n).getPosition().y, iceBlocksBackup.get(n).getFish()));
                iceBlocks.get(n).setPenguin(iceBlocksBackup.get(n).getPenguin());
            }

            int aloneModifier = 0;
            int lengthModifier = 1;
            if (isPenguinAlone(legalMoves.get(i).getStartBlock())) {aloneModifier = 1; lengthModifier = 5;}
            if (isPenguinReallyAlone(legalMoves.get(i).getStartBlock())) {aloneModifier = aloneModifier + 2; lengthModifier = 20;}

            // apply move to test environment
            highlightedBlock = legalMoves.get(i).getStartBlock();
            selectedBlock = legalMoves.get(i).getEndBlock();
            // determine movement axis
            if (highlightedBlock > -1 && iceBlocks.get(highlightedBlock).getRow('u') == iceBlocks.get(selectedBlock).getRow('u'))  {movementDirection = 'u';}
            else if (highlightedBlock > -1 && iceBlocks.get(highlightedBlock).getRow('d') == iceBlocks.get(selectedBlock).getRow('d'))  {movementDirection = 'd';}
            else if (highlightedBlock > -1 && iceBlocks.get(highlightedBlock).getRow('h') == iceBlocks.get(selectedBlock).getRow('h'))  {movementDirection = 'h';}
            else {movementDirection = 'z';}
            // determine movement valence
            if (highlightedBlock > -1 && iceBlocks.get(highlightedBlock).getPos(movementDirection) > iceBlocks.get(selectedBlock).getPos(movementDirection)) {directionFlag = false;}
            else {directionFlag = true;}
            // destroy ice blocks
            for (IceBlock block : iceBlocks) {
                if (iceBlocks.get(selectedBlock).getRow(movementDirection) == block.getRow(movementDirection) &&
                        ((block.getPos(movementDirection) > iceBlocks.get(highlightedBlock).getPos(movementDirection)) == directionFlag) &&
                        ((block.getPos(movementDirection) < iceBlocks.get(selectedBlock).getPos(movementDirection)) == directionFlag)) {
                    block.setFish(-1);
                }
            }
            int lengthOfMove = Math.abs(iceBlocks.get(highlightedBlock).getPos(movementDirection) - iceBlocks.get(selectedBlock).getPos(movementDirection));
            int scoreBonus = iceBlocks.get(selectedBlock).getScore() - 1;
            iceBlocks.get(highlightedBlock).setPenguin('n');
            iceBlocks.get(highlightedBlock).setFish(-1);
            highlightedBlock = -1;
            iceBlocks.get(selectedBlock).setPenguin(penguinChars[turn]);
            iceBlocks.get(selectedBlock).setFish(0);
            deadPenguinCheck();
            priorityList[i] = Math.random() + 0.2*scoreBonus - 0.1*(lengthOfMove-1)*lengthModifier; // seed + score bonus - length penalty
            if (getPlayersPenguinCount() < penguinCountPrior) {priorityList[i] = (priorityList[i] - 1.8);} // penalise killing own penguin
            priorityList[i] = priorityList[i] - aloneModifier*0.4; // penalise moving alone penguins
            if (getOtherPlayersPenguinCount() < otherPenguinCountPrior) {priorityList[i] = (priorityList[i] + 0.8 * (otherPenguinCountPrior - getOtherPlayersPenguinCount()));} // reward killing other penguins
            priorityList[i] = (priorityList[i] + (otherPlayersAverageDensityPrior - getOtherPlayersTotalDensity())*(6-Game.PLAYERS)*0.04); // reward reducing opponent's freedom
            priorityList[i] = (priorityList[i] - (playersAverageDensityPrior - getPlayersTotalDensity())*(6-Game.PLAYERS)*0.02); // penalise reducing own freedom
        }
        highlightedBlock = -1;
        // reset
        iceBlocks = new Array<IceBlock>();
        for(int n=0; n<60; n++) {
            iceBlocks.add(new IceBlock(n, (int) iceBlocksBackup.get(n).getPosition().x, (int) iceBlocksBackup.get(n).getPosition().y, iceBlocksBackup.get(n).getFish()));
            iceBlocks.get(n).setPenguin(iceBlocksBackup.get(n).getPenguin());
        }
        aiTestFlag = false;

        for (int i = 0; i < priorityList.length; i++) {if (priorityList[i] > maxRand) {selected = i; maxRand = priorityList[i];}}
        int[] aiMovePath = {legalMoves.get(selected).getStartBlock(),legalMoves.get(selected).getEndBlock()};
        return aiMovePath;
    }

// Place AI penguin
    private int getAiPlacement() {
        int aiSelectedBlock = -1;
        selectedBlock = 0;
        int threeCount = 0;
        int maxDistanceOverall = 0;
        int minDistance;
        int targetFish;
        for (int i = 0; i < 60; i++) {
            if (iceBlocks.get(i).getScore() == 3) {threeCount = threeCount + 1;}
        }

        if (Math.random() < 0.6 + (0.1*threeCount)) {targetFish = 3;} else {targetFish = 2;}

        for (int i = 0; i < 60; i++) {
            if (iceBlocks.get(i).getScore() == targetFish) {
                minDistance = 5000000;
                if (getPlayersPenguinCount() == 0) {aiSelectedBlock = i;}
                else {
                    for (int j = 0; j < 60; j++) {
                        if (iceBlocks.get(j).getPenguin() == penguinChars[turn] && ((int) Math.sqrt(iceBlocks.get(i).getDistance(iceBlocks.get(j).getPosition().x, 720-iceBlocks.get(j).getPosition().y))) < minDistance) {
                            minDistance = ((int) Math.sqrt(iceBlocks.get(i).getDistance(iceBlocks.get(j).getPosition().x,720- iceBlocks.get(j).getPosition().y)));
                        }
                    }
                    if (minDistance - ((int)(Math.random()*600-300)) > maxDistanceOverall) {maxDistanceOverall = minDistance; aiSelectedBlock = i;}
                }
            }
        }
        return aiSelectedBlock;
    }

// Check for new dead penguins for all players
    private void deadPenguinCheck() {
        for (int i = 0; i<60; i++) {
            if (iceBlocks.get(i).getPenguin() != 'n' && iceBlocks.get(i).getPenguin() != 'd') {
                legalFlag = false;
                for (IceBlock block2 : iceBlocks) {
                    if (Math.sqrt(Math.pow(iceBlocks.get(i).getPosition().x - block2.getPosition().x ,2)+Math.pow(iceBlocks.get(i).getPosition().y - block2.getPosition().y ,2)) < 93.2 && block2.getFish() > 0) {
                        legalFlag = true;
                    }
                }
                if(!legalFlag) {iceBlocks.get(i).setPenguin('d'); }
            }
        }
    }

// Check for end of fishFightGame (no non-dead penguins), then check if active player has any non-dead penguins (if not change player)
    private void legalMoveCheck() {
        for (int p=1; p <= Game.PLAYERS; p++) {

            // Check if end of fishFightGame
            legalFlag = false;
            for (IceBlock block : iceBlocks) {
                if (block.getPenguin() != 'n' && block.getPenguin() != 'd') {legalFlag = true;}
            }
            if (!legalFlag && !endFlag && !aiTestFlag) {endFlag = true; Game.fanfare.play(0.5f * Game.soundFlag); }

            //check for legal moves for active player

            legalFlag = false;
            if (getPlayersPenguinCount() > 0) {legalFlag = true; }
            if (!legalFlag) {turn = (turn + 1) % Game.PLAYERS;}
        }
    }

    @Override
    public void handleInput() {
        if(Gdx.input.justTouched()) {

            xInput = Game.WIDTH*Gdx.input.getX()/Gdx.graphics.getWidth();
            yInput = Game.HEIGHT*Gdx.input.getY()/Gdx.graphics.getHeight();

            // See if Quit button has been clicked
            if (xInput >= 1050 && xInput <= 1230 && yInput <= (720-50) && yInput >= (720-130)) {
                Game.click.play(0.5f * Game.soundFlag);
                gsm.set(new MenuState(gsm));
                dispose();
            }

            // See if Auto button has been clicked
            if (remainingPenguins == 0 && !endFlag && autoFlags[turn] == 0 && (!Game.aiFlag || turn == 0 ) && xInput >= 50 && xInput <= 230 && yInput <= (720-50) && yInput >= (720-130)) {
                Game.click.play(0.5f * Game.soundFlag);
                autoFlags[turn] = 1;
            }

            // See if Rematch button has been clicked
            if (endFlag && xInput >= 50 && xInput <= 230 && yInput <= (720-50) && yInput >= (720-130)) {
                Game.click.play(0.5f * Game.soundFlag);
                gsm.set(new PlayState(gsm));
                dispose();
            }

            // Music and sound buttons
            if (xInput >= 50 && xInput <= 130 && yInput <= (720-150) && yInput >= (720-210)) {
                Game.musicFlag = (Game.musicFlag + 1)%2;
                Game.musicLoop.setVolume(0.5f * Game.musicFlag);
            }
            if (xInput >= 150 && xInput <= 230 && yInput <= (720-150) && yInput >= (720-210)) {
                Game.soundFlag = (Game.soundFlag + 1)%2;
            }

            // determine selected block (-1 is no block selected)
            lastSelectedBlock = selectedBlock;
            int[] blockDistances = new int[60];
            for (IceBlock block : iceBlocks) {
                blockDistances[block.getBlockNumber()] = (block.getDistance( xInput, yInput));

            }
            int minDistance = (int) Math.pow(10,8);
            for (int i = 0; i<60; i++){
                if (blockDistances[i] < minDistance) {minDistance = blockDistances[i];}
            }
            for (int i = 0; i<60; i++){
                if (blockDistances[i] == minDistance) {selectedBlock = i;}
            }

            // make sure a block in range is selected
            if (Math.sqrt(minDistance) > 50) {selectedBlock = -1;}

            // if in play phase or placement phase
            if (remainingPenguins == 0)
                {
                highlightedBlock = -1;
                if (selectedBlock != -1)
                    {

                        // determine movement axis
                        if (lastValidSelectedBlock > -1 && iceBlocks.get(lastValidSelectedBlock).getRow('u') == iceBlocks.get(selectedBlock).getRow('u'))  {movementDirection = 'u';}
                        else if (lastValidSelectedBlock > -1 && iceBlocks.get(lastValidSelectedBlock).getRow('d') == iceBlocks.get(selectedBlock).getRow('d'))  {movementDirection = 'd';}
                        else if (lastValidSelectedBlock > -1 && iceBlocks.get(lastValidSelectedBlock).getRow('h') == iceBlocks.get(selectedBlock).getRow('h'))  {movementDirection = 'h';}
                        else {movementDirection = 'z';}

                        // determine if pos is increasing from lastValid to current selection = true
                        if (lastValidSelectedBlock > -1 && iceBlocks.get(lastValidSelectedBlock).getPos(movementDirection) > iceBlocks.get(selectedBlock).getPos(movementDirection)) {directionFlag = false;}
                        else {directionFlag = true;}

                    // if selected penguin of this team's colour, then select that penguin
                    if (iceBlocks.get(selectedBlock).getPenguin() == penguinChars[turn]) {
                        highlightedBlock = selectedBlock;
                        Game.click.play(0.5f * Game.soundFlag);
                        lastValidSelectedBlock = selectedBlock;
                    }

                    else if (
                            lastValidSelectedBlock > -1 &&
                            iceBlocks.get(selectedBlock).getFish() > 0 &
                            iceBlocks.get(lastValidSelectedBlock).getPenguin() == penguinChars[turn] &
                            lastValidSelectedBlock == lastSelectedBlock
                    ){
                        // check if move is legal

                        legalFlag = true;
                        for (IceBlock block : iceBlocks) {
                            if (movementDirection == 'z'
                                || block.getBlockNumber() != lastValidSelectedBlock
                                && block.getRow(movementDirection) == iceBlocks.get(selectedBlock).getRow(movementDirection)
                                && ((block.getPos(movementDirection) > iceBlocks.get(lastValidSelectedBlock).getPos(movementDirection)) == directionFlag)
                                && ((block.getPos(movementDirection) < iceBlocks.get(selectedBlock).getPos(movementDirection)) == directionFlag )
                                && block.getFish() < 1)
                            {legalFlag = false;}
                        }
                        if (!legalFlag) { Game.bong.play(1.2f * Game.soundFlag);}

                        if (legalFlag) {
                            Game.ding.play(0.5f * Game.soundFlag);
                            scores[turn] = (scores[turn] + iceBlocks.get(selectedBlock).getScore());
                            for (IceBlock block : iceBlocks) {
                                if (iceBlocks.get(selectedBlock).getRow(movementDirection) == block.getRow(movementDirection) &&
                                        ((block.getPos(movementDirection) > iceBlocks.get(lastValidSelectedBlock).getPos(movementDirection)) == directionFlag) &&
                                        ((block.getPos(movementDirection) < iceBlocks.get(selectedBlock).getPos(movementDirection)) == directionFlag)) {
                                    block.setFish(-1);
                                }

                            }
                            iceBlocks.get(lastValidSelectedBlock).setPenguin('n');
                            iceBlocks.get(lastValidSelectedBlock).setFish(-1);
                            iceBlocks.get(selectedBlock).setPenguin(penguinChars[turn]);
                            iceBlocks.get(selectedBlock).setFish(0);
                            lastValidSelectedBlock = selectedBlock;
                            turn = (turn + 1) % Game.PLAYERS;

                            /* CHECK FOR DEAD PENGUINS */
                            deadPenguinCheck();

                            // Check for legal moves
                            legalMoveCheck();

                        }
                    }
                }

            }
            //below is for placement stage
            else {
                if (selectedBlock > -1) {
                    if (iceBlocks.get(selectedBlock).getFish() > 0) {
                        Game.ding.play(0.5f * Game.soundFlag);
                        scores[turn] = scores[turn] + iceBlocks.get(selectedBlock).getScore();
                        iceBlocks.get(selectedBlock).setFish(0);
                        iceBlocks.get(selectedBlock).setPenguin(penguinChars[turn]);
                        remainingPenguins = remainingPenguins -1;
                        turn = (turn+1)%Game.PLAYERS;
                    }

                }
            }

        }
    }

    @Override
    public void update(float dt) {

        //Deal with auto-moving
        if (System.currentTimeMillis() > endPauseTime || endFlag) {

            if (autoFlags[turn] == 1 && highlightedBlock != -1 && !endFlag) {
                int minDensity = 50;
                int maxDensity = -1;
                int maxDensityBlock = 0;
                int minDensityBlock = 0;
                for (int i = 0; i < 60; i++) {
                    if (Math.sqrt(Math.pow(iceBlocks.get(i).getPosition().x - iceBlocks.get(highlightedBlock).getPosition().x,2)+Math.pow(iceBlocks.get(i).getPosition().y - iceBlocks.get(highlightedBlock).getPosition().y,2)) < 93.2 && iceBlocks.get(i).getFish() > 0) {
                        if (getDensity(i) < minDensity) {
                            minDensity = getDensity(i);
                            minDensityBlock = i;
                        }
                        if (getDensity(i) > maxDensity) {
                            maxDensity = getDensity(i);
                            maxDensityBlock = i;
                        }
                    }
                }

                if (getDensity(minDensityBlock) == 0 && getDensity(maxDensityBlock) != 0 ) {selectedBlock = maxDensityBlock;}
                else {selectedBlock = minDensityBlock;}
                //if (Game.aiFlag && turn != 0) {autoFlags[turn] = 0;}
                scores[turn] = (scores[turn] + iceBlocks.get(selectedBlock).getScore());
                iceBlocks.get(highlightedBlock).setPenguin('n');
                iceBlocks.get(highlightedBlock).setFish(-1);
                highlightedBlock = -1;
                iceBlocks.get(selectedBlock).setPenguin(penguinChars[turn]);
                iceBlocks.get(selectedBlock).setFish(0);
                lastValidSelectedBlock = selectedBlock;
                turn = (turn + 1) % Game.PLAYERS;
                deadPenguinCheck();
                legalMoveCheck();
            }

            if (autoFlags[turn] == 1 && highlightedBlock == -1) {
                autoPenguin = selectRemainingPenguin();
                highlightedBlock = autoPenguin;
                endPauseTime = System.currentTimeMillis() + 500;
            }

            // AI set auto if needed
            if (Game.aiFlag && turn != 0 && autoFlags[turn] == 0 && remainingPenguins == 0 && playersPenguinsAllReallyAlone()) {autoFlags[turn] = 1; System.out.println("AI auto flag set");}

            // AI Move
            if (Game.aiFlag && turn != 0 && autoFlags[turn] == 0 && highlightedBlock != -1 && remainingPenguins == 0) {
                selectedBlock =  aiMovePath[1];
                // determine movement axis
                if (highlightedBlock > -1 && iceBlocks.get(highlightedBlock).getRow('u') == iceBlocks.get(selectedBlock).getRow('u'))  {movementDirection = 'u';}
                else if (highlightedBlock > -1 && iceBlocks.get(highlightedBlock).getRow('d') == iceBlocks.get(selectedBlock).getRow('d'))  {movementDirection = 'd';}
                else if (highlightedBlock > -1 && iceBlocks.get(highlightedBlock).getRow('h') == iceBlocks.get(selectedBlock).getRow('h'))  {movementDirection = 'h';}
                else {movementDirection = 'z';}
                // determine movement valence
                if (highlightedBlock > -1 && iceBlocks.get(highlightedBlock).getPos(movementDirection) > iceBlocks.get(selectedBlock).getPos(movementDirection)) {directionFlag = false;}
                else {directionFlag = true;}
                for (IceBlock block : iceBlocks) {
                    if (iceBlocks.get(selectedBlock).getRow(movementDirection) == block.getRow(movementDirection) &&
                            ((block.getPos(movementDirection) > iceBlocks.get(highlightedBlock).getPos(movementDirection)) == directionFlag) &&
                            ((block.getPos(movementDirection) < iceBlocks.get(selectedBlock).getPos(movementDirection)) == directionFlag)) {
                        block.setFish(-1);
                    }
                }
                scores[turn] = (scores[turn] + iceBlocks.get(selectedBlock).getScore());
                iceBlocks.get(highlightedBlock).setPenguin('n');
                iceBlocks.get(highlightedBlock).setFish(-1);
                highlightedBlock = -1;
                iceBlocks.get(selectedBlock).setPenguin(penguinChars[turn]);
                iceBlocks.get(selectedBlock).setFish(0);
                lastValidSelectedBlock = selectedBlock;
                turn = (turn + 1) % Game.PLAYERS;
                deadPenguinCheck();
                legalMoveCheck();
                if (turn != 0 && !endFlag) {endPauseTime = System.currentTimeMillis() + 500;}
            }

            // AI highlight
            if (Game.aiFlag && turn != 0 && autoFlags[turn] == 0 && highlightedBlock == -1 && remainingPenguins == 0) {
                aiMovePath = getAiMovePath();
                highlightedBlock = aiMovePath[0];
                endPauseTime = System.currentTimeMillis() + 500;
            }

            // AI placement
            if (Game.aiFlag && turn != 0 && remainingPenguins != 0) {
                selectedBlock = getAiPlacement();
                scores[turn] = scores[turn] + iceBlocks.get(selectedBlock).getScore();
                iceBlocks.get(selectedBlock).setFish(0);
                iceBlocks.get(selectedBlock).setPenguin(penguinChars[turn]);
                remainingPenguins = remainingPenguins -1;
                turn = (turn+1)%Game.PLAYERS;
                if (turn != 0) {endPauseTime = System.currentTimeMillis() + 500;}
            }

            //Handle input for non-auto move or end of fishFightGame
            if (System.currentTimeMillis() > endPauseTime || endFlag ) {handleInput();}
        }


    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(background,0,0);
        for (IceBlock block : iceBlocks){

            if (block.getFish() > -1) {sb.draw(iceBlockTexture, block.getPosition().x, block.getPosition().y);}
            if (block.getBlockNumber() == highlightedBlock) {sb.draw(highlightTexture, block.getPosition().x, block.getPosition().y);}
            if (block.getFish() > 0) {
                if (block.getFish() < 11) {sb.draw(threeFishTexture, block.getPosition().x + 10, block.getPosition().y +25,74,65);}
                else if (block.getFish() <31) {sb.draw(twoFishTexture, block.getPosition().x + 10, block.getPosition().y +25,74,61);}
                else {sb.draw(oneFishTexture, block.getPosition().x + 10, block.getPosition().y +35,74,40);}
            }
            if (block.getPenguin() == 'r') {sb.draw(redPenguinTexture, block.getPosition().x + PENGUIN_OFFSET.x, block.getPosition().y + PENGUIN_OFFSET.y,PENGUIN_SIZE.x,PENGUIN_SIZE.y);}
            else if (block.getPenguin() == 'b') {sb.draw(bluePenguinTexture, block.getPosition().x + PENGUIN_OFFSET.x, block.getPosition().y + PENGUIN_OFFSET.y,PENGUIN_SIZE.x,PENGUIN_SIZE.y);}
            else if (block.getPenguin() == 'g') {sb.draw(greenPenguinTexture, block.getPosition().x + PENGUIN_OFFSET.x, block.getPosition().y + PENGUIN_OFFSET.y,PENGUIN_SIZE.x,PENGUIN_SIZE.y);}
            else if (block.getPenguin() == 'y') {sb.draw(yellowPenguinTexture, block.getPosition().x + PENGUIN_OFFSET.x, block.getPosition().y + PENGUIN_OFFSET.y,PENGUIN_SIZE.x,PENGUIN_SIZE.y);}
            else if (block.getPenguin() == 'd') {sb.draw(deadPenguinTexture, block.getPosition().x + PENGUIN_OFFSET.x, block.getPosition().y + PENGUIN_OFFSET.y,PENGUIN_SIZE.x,PENGUIN_SIZE.y);}
        }

        sb.draw(currentPlayerTexture, 1020, 500,240,154);
        sb.draw(quitTexture, 1050, 50,180,80);
        if (remainingPenguins == 0 && autoFlags[turn] == 0 && !endFlag && (!Game.aiFlag || turn == 0 )) {sb.draw(autoTexture, 50, 50,180,80);}
        if (endFlag) {sb.draw(rematchTexture, 50, 50,180,80);}
        sb.draw(squareTexture, 50, 150,80,80);
        sb.draw(squareTexture, 150, 150,80,80);
        sb.draw(musicTexture, 50, 150,80,80);
        sb.draw(speakerTexture, 150, 150,80,80);
        if (Game.musicFlag == 0) {sb.draw(crossTexture, 50, 150,80,80);}
        if (Game.soundFlag == 0) {sb.draw(crossTexture, 150, 150,80,80);}

        if (turn == 0 && !endFlag) {sb.draw(redPenguinBig, 1020, 160,256,320);}
        else if (turn == 1 && !endFlag) {sb.draw(bluePenguinBig, 1020, 160,256,320);}
        else if (turn == 2 && !endFlag) {sb.draw(greenPenguinBig, 1020, 160,256,320);}
        else if (turn == 3 && !endFlag) {sb.draw(yellowPenguinBig, 1020, 160,256,320);}

        for (int playerCounter = 0; playerCounter < Game.PLAYERS; playerCounter++){
            sb.draw(squareTexture,50,600-playerCounter*(PENGUIN_SIZE.y +20), PENGUIN_SIZE.x +10, PENGUIN_SIZE.y +10);
            if (playerCounter == 0) {sb.draw(redPenguinTexture,55,605-playerCounter*(PENGUIN_SIZE.y +20), PENGUIN_SIZE.x , PENGUIN_SIZE.y );}
            else if (playerCounter == 1) {sb.draw(bluePenguinTexture,55,605-playerCounter*(PENGUIN_SIZE.y +20), PENGUIN_SIZE.x , PENGUIN_SIZE.y );}
            else if (playerCounter == 2) {sb.draw(greenPenguinTexture,55,605-playerCounter*(PENGUIN_SIZE.y +20), PENGUIN_SIZE.x , PENGUIN_SIZE.y );}
            else if (playerCounter == 3) {sb.draw(yellowPenguinTexture,55,605-playerCounter*(PENGUIN_SIZE.y +20), PENGUIN_SIZE.x , PENGUIN_SIZE.y );}
            sb.draw(new TextureRegion(scoreRegion,141*((int) Math.floor(scores[playerCounter]/10)),0,141,200),50+PENGUIN_SIZE.x +10,600-playerCounter*(PENGUIN_SIZE.y +20),141*(PENGUIN_SIZE.y+10)/200,PENGUIN_SIZE.y+10);
            sb.draw(new TextureRegion(scoreRegion,141*(scores[playerCounter]%10),0,141,200),50+PENGUIN_SIZE.x +10+141*(PENGUIN_SIZE.y+10)/200,600-playerCounter*(PENGUIN_SIZE.y +20),141*(PENGUIN_SIZE.y+10)/200,PENGUIN_SIZE.y+10);
        }

        if (endFlag) {

            maxScore = 0;
            for (int i = 0; i < 4; i++) {
                if (scores[i] > maxScore) {maxScore = scores[i];}
            }

            for (int i = 0; i < 4; i++) {
                if (scores[i] == maxScore && scores[i] != scores[(i+1)%4] && scores[i] != scores[(i+2)%4] && scores[i] != scores[(i+3)%4]) {drawFlag = false;}
            }

            if (drawFlag) {sb.draw(new TextureRegion(winScreen,0,4*winScreen.getHeight()/5,winScreen.getWidth(),winScreen.getHeight()/5),Game.WIDTH/2 - winScreen.getWidth()/2,Game.HEIGHT/2 - winScreen.getHeight()/10); sb.draw(deadPenguinBig, 1020, 160,256,320);}
            else {
                for (int i = 0; i < 4; i++) {
                    if (maxScore == scores[i]) {
                        sb.draw(new TextureRegion(winScreen,0,i*winScreen.getHeight()/5,winScreen.getWidth(),winScreen.getHeight()/5),Game.WIDTH/2 - winScreen.getWidth()/2,Game.HEIGHT/2 - winScreen.getHeight()/10);
                        if (i == 0) {sb.draw(redPenguinBig, 1020, 160,256,320);}
                        else if (i == 1) {sb.draw(bluePenguinBig, 1020, 160,256,320);}
                        else if (i == 2) {sb.draw(greenPenguinBig, 1020, 160,256,320);}
                        else if (i == 3) {sb.draw(yellowPenguinBig, 1020, 160,256,320);}
                    }
                }
            }
        }

        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        iceBlockTexture.dispose();
        oneFishTexture.dispose();
        twoFishTexture.dispose();
        threeFishTexture.dispose();
        deadPenguinTexture.dispose();
        deadPenguinBig.dispose();
        redPenguinTexture.dispose();
        bluePenguinTexture.dispose();
        greenPenguinTexture.dispose();
        yellowPenguinTexture.dispose();
        highlightTexture.dispose();
        redPenguinBig.dispose();
        bluePenguinBig.dispose();
        greenPenguinBig.dispose();
        yellowPenguinBig.dispose();
        quitTexture.dispose();
        autoTexture.dispose();
        squareTexture.dispose();
        currentPlayerTexture.dispose();
        scoreRegion.dispose();
        winScreen.dispose();
        rematchTexture.dispose();
        crossTexture.dispose();
        musicTexture.dispose();
        speakerTexture.dispose();
    }
}
