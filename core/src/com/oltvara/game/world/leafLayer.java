package com.oltvara.game.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.oltvara.game.entities.sprite;

import static com.oltvara.game.mainGame.trTex;

public class leafLayer extends sprite {

    private Color col;

    public leafLayer(Vector2 pos, Array<TextureAtlas.AtlasRegion> tex, Color col) {
        super(pos);
        this.col = col;

        TextureRegion[] sprites = tex.toArray();
        setAnim(sprites, 1 / 12f);
    }

    public Color getCol() {
        return col;
    }
}
