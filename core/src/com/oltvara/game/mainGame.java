package com.oltvara.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.oltvara.game.handlers.*;

public class mainGame extends ApplicationAdapter {

	public static final String TITLE = "Olt' Vara";
	public static final int cWIDTH = 480;
	public static final int cHEIGHT = 360;
	public static final int SCALE = 2;
	public static final float TILESIZE = 16;

	private static final float TICK = 1 / 60f;
	private float elapsedTime;

	private SpriteBatch batch;
	private OrthographicCamera mainCam;
	private OrthographicCamera uiCam;
	private Viewport viewport;

	public OrthographicCamera getMainCam() { return mainCam; }
	public OrthographicCamera getUiCam() { return uiCam; }
	public SpriteBatch getBatch() { return batch; }

	private stateHandler GSH;

	public static srcHandler src;

	public static maths fct;

	public static TextureAtlas groundAtlas;

	@Override
	public void create() {

		Gdx.input.setInputProcessor(new inputHandler());

		fct = new maths();

		src = new srcHandler();
		src.importTX("resources/entities/charTest.png", "mainChar");

		groundAtlas = new TextureAtlas(Gdx.files.internal("resources/tiles/groundTiles.atlas"));

		batch = new SpriteBatch();
		mainCam = new OrthographicCamera();
		mainCam.setToOrtho(false, cWIDTH, cHEIGHT);
		viewport = new FitViewport(cWIDTH, cHEIGHT, mainCam);
		uiCam = new OrthographicCamera();
		uiCam.setToOrtho(false, cWIDTH, cHEIGHT);

		GSH = new stateHandler(this);
	}

	@Override
	public void render() {

		elapsedTime += Gdx.graphics.getDeltaTime();
		while (elapsedTime >= TICK) {
			elapsedTime -= TICK;
			GSH.update(TICK);
			GSH.render();
			inputControl.update();
		}
	}

	@Override
	public void resize (int width, int height) {
		viewport.update(width, height);
	}
	
	@Override
	public void dispose() {

	}
}
