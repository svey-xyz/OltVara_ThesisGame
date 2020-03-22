package com.oltvara.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.oltvara.game.handlers.*;
import com.oltvara.game.world.wrldHandlers.forestTextureLoader;

public class mainGame extends ApplicationAdapter {

	public static final String TITLE = "Olt' Vara";

	private static final float dWIDTH = 1920;
	private static final float dHEIGHT = 1080;
	private static final float RATIO = dWIDTH / dHEIGHT;

	public static final int TILESIZE = 16;
	public static final int numTILES = 33;
	public static final int numPowerTILES = 32;
	public static final int tileBUFFER = 6;

	public static final int cWIDTH = TILESIZE * numTILES - (TILESIZE * tileBUFFER);
	public static final int cHEIGHT = (int)(cWIDTH / RATIO);

	public static final int SCALE = (int)(dWIDTH / cWIDTH);

	public static final float TICK = 1 / 60f;
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



	public static forestTextureLoader frTex;

	public mainGame() {
	}

	@Override
	public void create() {

		Gdx.input.setInputProcessor(new inputHandler());

		fct = new maths();
		src = new srcHandler();
		frTex = new forestTextureLoader();

		src.importTX("resources/entities/charTest.png", "mainChar");

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
			GSH.update(TICK);
			inputControl.update();
			elapsedTime -= TICK;
		}
		GSH.render();
	}

	@Override
	public void resize (int width, int height) {
		viewport.update(width, height);
	}
	
	@Override
	public void dispose() {
		frTex.dispose();
	}
}
