package com.oltvara.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.oltvara.game.mainGame;

public class mainChar extends sprite {

    public mainChar(Body body) {
        super(body);

        Texture tex = mainGame.src.getTX("mainChar");
        TextureRegion[] sprites = TextureRegion.split(tex, 64, 64)[0];
        setAnim(sprites, 1 / 12f, 0);
    }
}
