package com.oltvara.game.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.oltvara.game.entities.sprite;

class leafLayer extends sprite {

    private Color col;

    leafLayer(Vector2 pos, Array<TextureAtlas.AtlasRegion> tex, Color col, int animOffset) {
        super(pos);
        this.col = col;

        TextureRegion[] sprites = tex.toArray();
        setAnim(sprites, 1 / 12f, animOffset);
    }

    Color getCol() {
        return col;
    }
}
