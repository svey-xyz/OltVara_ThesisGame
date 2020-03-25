package com.oltvara.game.gamestates;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.oltvara.game.handlers.stateHandler;
import com.oltvara.game.mainGame;

public abstract class gameState {

    protected stateHandler GSH;
    protected mainGame game;

    protected SpriteBatch batch;
    protected OrthographicCamera mainCam;
    protected OrthographicCamera uiCam;

    protected gameState(stateHandler GSH) {
        this.GSH = GSH;
        game = GSH.game();
        batch = game.getBatch();
        mainCam = game.getMainCam();
        uiCam = game.getUiCam();

    }

    public abstract void handleInput(float delta);

    public abstract void update(float delta);

    public abstract void render();

    public abstract void dispose();

}
