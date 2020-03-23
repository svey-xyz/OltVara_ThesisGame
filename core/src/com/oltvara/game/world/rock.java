package com.oltvara.game.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import static com.oltvara.game.mainGame.TILESIZE;

public class rock {

    private TextureRegion tx, mossTX;
    private Color mossCol;
    private final Vector2 renPOS;
    private int size;

    public rock(Vector2 pos, int chunkOffset, TextureRegion tx, TextureRegion mossTX, Color mossCol) {
        this.renPOS = new Vector2(TILESIZE*(pos.x + chunkOffset), TILESIZE*(pos.y) + TILESIZE);
        this.tx = tx;
        this.mossTX = mossTX;
        this.mossCol = mossCol;

        size = tx.getRegionWidth();
    }

    public TextureRegion getTX() { return tx; }

    public TextureRegion getMossTX() { return mossTX; }

    public Color getMossCol() { return mossCol; }

    public Vector2 getRenPOS() {
        return renPOS;
    }

    public int getSize() {
        return size;
    }

}
