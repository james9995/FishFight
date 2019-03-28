package com.mygdx.fishFightGame.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.fishFightGame.Game;

public class MenuState extends State {
    private Texture background, menu1, menu2, musicCredit;
    private int xInput, yInput, currentMenu;

    public MenuState(GameStateManager gsm) {
        super(gsm);
        cam.setToOrtho(false, Game.WIDTH,Game.HEIGHT);
        background = new Texture("background2.png");
        menu1 = new Texture("menu1.png");
        menu2 = new Texture("menu2.png");
        musicCredit = new Texture("musicCredit.png");
        xInput = 0;
        yInput = 0;
        currentMenu = 1;
    }

    @Override
    public void handleInput() {
        if(Gdx.input.justTouched()) {
            xInput = Game.WIDTH*Gdx.input.getX()/Gdx.graphics.getWidth();
            yInput = Game.HEIGHT*Gdx.input.getY()/Gdx.graphics.getHeight();

            if (currentMenu == 2) {
                // 2 player
                if (
                        xInput >= 394
                        && xInput <= 542
                        && yInput >= 348
                        && yInput <= 464
                ) {
                    Game.click.play(0.5f * Game.soundFlag);
                    Game.PLAYERS = 2;
                    gsm.set(new PlayState(gsm));
                    dispose();
                }

                // 3 player
                if (
                        xInput >= 564
                                && xInput <= 710
                                && yInput >= 348
                                && yInput <= 464
                ) {
                    Game.click.play(0.5f * Game.soundFlag);
                    Game.PLAYERS = 3;
                    gsm.set(new PlayState(gsm));
                    dispose();
                }

                // 4 player
                if (
                        xInput >= 732
                                && xInput <= 880
                                && yInput >= 348
                                && yInput <= 464
                ) {
                    Game.click.play(0.5f * Game.soundFlag);
                    Game.PLAYERS = 4;
                    gsm.set(new PlayState(gsm));
                    dispose();
                }

            }

            if (currentMenu == 1) {

                // Local Multi player
                if (
                        xInput >= 264
                        && xInput <= 1016
                        && yInput >= 92
                        && yInput <= 206
                ) {Game.click.play(0.5f * Game.soundFlag); currentMenu = 2; Game.aiFlag = false;}

                // Game vs AI
                if (
                        xInput >= 264
                                && xInput <= 1016
                                && yInput >= 227
                                && yInput <= 342
                ) {Game.click.play(0.5f * Game.soundFlag); currentMenu = 2; Game.aiFlag = true;}

                // Rules
                if (
                        xInput >= 264
                                && xInput <= 1016
                                && yInput >= 362
                                && yInput <= 477
                ) {Game.click.play(0.5f * Game.soundFlag);
                    gsm.push(new RulesState(gsm));
                    //dispose();
                    }

                // Quit
                if (
                        xInput >= 264
                                && xInput <= 1016
                                && yInput >= 498
                                && yInput <= 612
                ) {Game.click.play(0.5f * Game.soundFlag); dispose(); Gdx.app.exit();}

            }
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(background, 0,0 , Game.WIDTH, Game.HEIGHT );
        sb.draw(musicCredit, Game.WIDTH/2 - musicCredit.getWidth()/2,0);
        if (currentMenu == 1) {sb.draw(menu1, 0,0 , Game.WIDTH, Game.HEIGHT );}
        if (currentMenu == 2) {sb.draw(menu2, 0,0 , Game.WIDTH, Game.HEIGHT );}
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        menu1.dispose();
        menu2.dispose();
        musicCredit.dispose();
    }
}
