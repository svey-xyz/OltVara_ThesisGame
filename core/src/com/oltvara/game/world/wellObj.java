package com.oltvara.game.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.oltvara.game.handlers.physicsVars;

import static com.oltvara.game.handlers.physicsVars.PPM;
import static com.oltvara.game.mainGame.TILESIZE;

public class wellObj {
    private TextureRegion tx, mossTX, rockTX, rockMossTX;
    private Color mossCol, rockMossCol;
    private final Vector2 renPOS, POS;
    private int size;

    public wellObj(Vector2 pos, int chunkOffset, TextureRegion tx, TextureRegion mossTX, TextureRegion rockTX, TextureRegion rockMossTX, Color mossCol, Color rockMossCol) {
        this.size = tx.getRegionWidth();

        this.renPOS = new Vector2(((pos.x + 0.5f + chunkOffset) * TILESIZE / PPM - 2 / PPM) * PPM - size / 2f,
                ((pos.y + 0.5f) * TILESIZE / PPM - 2 / PPM) * PPM);


        this.POS = pos;
        this.tx = tx;
        this.mossTX = mossTX;
        this.rockTX = rockTX;
        this.rockMossTX = rockMossTX;
        this.mossCol = mossCol;
        this.rockMossCol = rockMossCol;


    }

    public BodyDef createBodDef(int off) {
        BodyDef defBod = new BodyDef();

        defBod.type = BodyDef.BodyType.StaticBody;
        defBod.position.set(new Vector2((POS.x + 0.5f + off) * TILESIZE / PPM, (POS.y + 0.5f) * TILESIZE / PPM));

        return defBod;
    }

    public FixtureDef createFix() {
        FixtureDef defFix = new FixtureDef();
        ChainShape cs = new ChainShape();
        Vector2[] v = new Vector2[4];

        v[0] = new Vector2(-size / 6f / PPM, -size / 3.3f / PPM);
        v[1] = new Vector2(-size / 6f / PPM, size / 3.3f / PPM);
        v[2] = new Vector2(size / 7.5f / PPM, size / 3.3f / PPM);
        v[3] = new Vector2(size / 7.5f / PPM, -size / 3.3f / PPM);

        cs.createChain(v);
        defFix.shape = cs;

        defFix.friction = 0;
        defFix.filter.categoryBits = physicsVars.bitGROUND;
        defFix.filter.maskBits = -1;
        defFix.isSensor = false;

        return defFix;
    }

    public TextureRegion getTX() { return tx; }

    public TextureRegion getMossTX() { return mossTX; }

    public TextureRegion getRockTX() { return rockTX; }

    public TextureRegion getRockMossTX() { return rockMossTX; }

    public Color getMossCol() { return mossCol; }

    public Color getRockMossCol() { return rockMossCol; }

    public Vector2 getRenPOS() {
        return renPOS;
    }

    public int getSize() {
        return size;
    }

}
