package com.mygdx.fishFightGame.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.fishFightGame.Game;

public class RulesState extends State {
    private Texture background, rules;

    public RulesState(GameStateManager gsm) {
        super(gsm);
        cam.setToOrtho(false, Game.WIDTH,Game.HEIGHT);
        background = new Texture("background2.png");
        rules = new Texture("rules.png");
    }

    @Override
    public void handleInput() {
        if(Gdx.input.justTouched()) {
            Game.click.play(0.5f * Game.soundFlag);
            //gsm.set(new MenuState(gsm));
            gsm.pop();
            dispose();
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
        sb.draw(rules, 0,0 , Game.WIDTH, Game.HEIGHT );
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        rules.dispose();
    }
}
