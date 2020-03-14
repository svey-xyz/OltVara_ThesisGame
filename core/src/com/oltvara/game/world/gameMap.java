package com.oltvara.game.world;

import com.badlogic.gdx.graphics.OrthographicCamera;

public abstract class gameMap {

    public abstract void render (OrthographicCamera camera);

    public abstract void update (float delta);

    public abstract void dispose ();

}
