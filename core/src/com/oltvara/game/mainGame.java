package com.oltvara.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.oltvara.game.handlers.*;
import com.oltvara.game.world.wrldHandlers.treeTextureLoader;

public class mainGame extends ApplicationAdapter {

	public static final String TITLE = "Olt' Vara";

	private static final float dWIDTH = 1920;
	private static final float dHEIGHT = 1080;
	private static final float RATIO = dWIDTH / dHEIGHT;

	public static final int TILESIZE = 16;
	public static final int numTILES = 33;
	public static final int numPowerTILES = 32;
	public static final int tileBUFFER = 4;

	public static final int cWIDTH = (int)(TILESIZE * numTILES - (TILESIZE * tileBUFFER));
	public static final int cHEIGHT = (int)(cWIDTH / RATIO);

	public static final int SCALE = (int)(dWIDTH / cWIDTH);

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

	public static treeTextureLoader trTex;

	public mainGame() {
		System.out.println(cWIDTH);
	}

	@Override
	public void create() {

		Gdx.input.setInputProcessor(new inputHandler());

		fct = new maths();
		src = new srcHandler();
		trTex = new treeTextureLoader();

		src.importTX("resources/entities/charTest.png", "mainChar");
		src.importTX("resources/environment/trees/1-1-1/layer1.png", "leaves-1-1-1");

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
