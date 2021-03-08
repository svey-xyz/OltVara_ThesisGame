package com.oltvara.game.world;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.oltvara.game.handlers.physicsVars;

import static com.oltvara.game.mainGame.*;
import static com.oltvara.game.handlers.physicsVars.PPM;

public class tile {

    private TextureRegion tx, grassTX;
    private final Vector2 POS, relPOS;
    private Vector2 flip;

    public tile(Vector2 pos, int offset) {
        this.POS = pos;
        this.relPOS = new Vector2(TILESIZE*(pos.x + offset), TILESIZE*(pos.y));

        flip = new Vector2(1, 1);
    }

    public void updateTexture(TextureRegion tx, int xFlip, int yFlip) {
        flip = new Vector2(xFlip, yFlip);
        this.tx = tx;
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

        v[0] = new Vector2(-TILESIZE / 2f / PPM, -TILESIZE / 2f / PPM);
        v[1] = new Vector2(-TILESIZE / 2f / PPM, TILESIZE / 2f / PPM);
        v[2] = new Vector2(TILESIZE / 2f / PPM, TILESIZE / 2f / PPM);
        v[3] = new Vector2(TILESIZE / 2f / PPM, -TILESIZE / 2f / PPM);

        cs.createChain(v);
        defFix.shape = cs;

        defFix.friction = 0;
        defFix.filter.categoryBits = physicsVars.bitGROUND;
        defFix.filter.maskBits = -1;
        defFix.isSensor = false;

        return defFix;
    }

    public void addGrass(TextureRegion tx) { grassTX = tx; }
    public Vector2 getPosition() { return POS; }
    public Vector2 getRelPOS() { return relPOS; }
    public Vector2 getFlip() { return flip; }
    public TextureRegion getTX() { return tx; }
    public TextureRegion getGrassTX() { return grassTX; }

}
