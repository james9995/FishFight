package com.mygdx.fishFightGame.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.fishFightGame.Game;

public abstract class State {
    public OrthographicCamera cam;
    public Vector2 mouse;
    public GameStateManager gsm;

    // Constructor for class "State"
    public State(GameStateManager gsm) {
        this.gsm = gsm;
        cam = new OrthographicCamera(Game.WIDTH,Game.HEIGHT);
        mouse = new Vector2();
    }

    // Methods for class "State"
    public abstract void handleInput();
    public abstract void update(float dt);
    public abstract void render(SpriteBatch sb);
    public abstract void dispose();

}
