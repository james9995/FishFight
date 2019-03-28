package com.mygdx.fishFightGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.fishFightGame.states.GameStateManager;
import com.mygdx.fishFightGame.states.MenuState;

public class Game extends ApplicationAdapter {
	public static final int WIDTH = 1280, HEIGHT = 720;
	public static final String TITLE = "Fish Fight";
    public static int PLAYERS = 2, soundFlag = 1, musicFlag = 1;
    public static boolean aiFlag = false;
    public static Sound click, ding, bong, fanfare;
    public static Music musicLoop;


	private GameStateManager gsm;
	private SpriteBatch sb;
	
	@Override
	public void create () {
		sb = new SpriteBatch();
		gsm = new GameStateManager();
		gsm.push(new MenuState(gsm));
        click = Gdx.audio.newSound(Gdx.files.internal("click.wav"));
        ding = Gdx.audio.newSound(Gdx.files.internal("ding.wav"));
        bong = Gdx.audio.newSound(Gdx.files.internal("bong.wav"));
        fanfare = Gdx.audio.newSound(Gdx.files.internal("fanfare.wav"));
        musicLoop = Gdx.audio.newMusic(Gdx.files.internal("jesseSpillaneLoop.mp3"));
        musicLoop.setLooping(true);
        musicLoop.setVolume(0.5f*Game.musicFlag);
        musicLoop.play();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 1,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gsm.update(Gdx.graphics.getDeltaTime());
        gsm.render(sb);
	}

	@Override
	public void dispose () {
		sb.dispose();
		click.dispose();
		bong.dispose();
		ding.dispose();
		fanfare.dispose();
		musicLoop.dispose();
	}
}
