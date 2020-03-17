package com.oltvara.game.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.oltvara.game.gamestates.play;
import com.oltvara.game.world.wrldHandlers.chunk;
import com.oltvara.game.world.wrldHandlers.physicsVars;
import com.oltvara.game.mainGame;

import static com.oltvara.game.mainGame.TILESIZE;
import static com.oltvara.game.world.wrldHandlers.physicsVars.PPM;
import static com.oltvara.game.mainGame.fct;

public class tile {

    private TextureRegion tx, grassTX;
    public String[] grassNames, groundNames, liveGroundNames, rockNames, liveDGroundNames;
    private FixtureDef defFix;
    private final Vector2 POS;
    private Vector2 relPOS;
    private final int OFFSET;
    private double rand;
    private int txPick;
    private int xFLIP = 1, yFLIP = 1;
    private final String EDGE;

    public tile(Vector2 pos, int offset, String edge) {
        this.POS = pos;
        this.OFFSET = offset;
        this.EDGE = edge;

        relPOS = new Vector2();
        relPOS.x = (POS.x + 0.5f + OFFSET) * TILESIZE / PPM;
        relPOS.y = (POS.y + 0.5f) * TILESIZE / PPM;

        grassNames = new String[]{"grass-1-1", "grass-1-2", "grass-1-3"};
        groundNames = new String[]{"ground-1-1", "ground-1-2", "groundSpeckle-1-1", "groundSpeckle-1-2"};
        rockNames = new String[]{"groundRock-1-1", "groundRockSpeckle-1-1"};
        liveGroundNames = new String[]{"groundLive-1-1", "groundLiveSpeckle-1-1"};
        liveDGroundNames = new String[]{"groundDLive-1-1", "groundDLiveSpeckle-1-1"};
    }

    public void updateTexture(String[] txChoice, int xFlip, int yFlip) {
        this.xFLIP = xFlip;
        this.yFLIP = yFlip;

        txPick = (int)fct.random(0, txChoice.length - 1);
        tx = mainGame.groundAtlas.findRegion(txChoice[txPick]);
    }

    public void render(SpriteBatch sb) {
        if (tx == null) { return; }
        sb.begin();
        sb.setColor(Color.WHITE);
        sb.draw(
                tx,
                (relPOS.x) * physicsVars.PPM - TILESIZE / 2f,
                (relPOS.y) * physicsVars.PPM - TILESIZE / 2f,
                8, 8, 16, 16, xFLIP, yFLIP, 0
        );
        if (grassTX != null) {
            sb.draw(
                    grassTX,
                    (relPOS.x) * physicsVars.PPM - TILESIZE / 2f,
                    (relPOS.y) * physicsVars.PPM - TILESIZE / 2f,
                    8, 8, 16, 16, 1, 1, 0
            );
        }

        sb.end();
    }

    public BodyDef createBodDef() {
        BodyDef defBod = new BodyDef();

        defBod.type = BodyDef.BodyType.StaticBody;
        defBod.position.set(relPOS);
        defFix  = new FixtureDef();

        return defBod;
    }

    public FixtureDef createFix() {

        //PolygonShape box = new PolygonShape();

        ChainShape cs = new ChainShape();
        Vector2[] v = new Vector2[4];

        v[0] = new Vector2(-mainGame.TILESIZE / 2f / PPM, -mainGame.TILESIZE / 2f / PPM);
        v[1] = new Vector2(-mainGame.TILESIZE / 2f / PPM, mainGame.TILESIZE / 2f / PPM);
        v[2] = new Vector2(mainGame.TILESIZE / 2f / PPM, mainGame.TILESIZE / 2f / PPM);
        v[3] = new Vector2(mainGame.TILESIZE / 2f / PPM, -mainGame.TILESIZE / 2f / PPM);

        cs.createChain(v);
        defFix.shape = cs;

        defFix.friction = 0;
        defFix.filter.categoryBits = physicsVars.bitGROUND;
        defFix.filter.maskBits = -1;
        defFix.isSensor = false;

        return defFix;
    }

    public void addGrass() {
        txPick = (int)fct.random(0, grassNames.length - 1);
        grassTX = mainGame.groundAtlas.findRegion(grassNames[txPick]);
    }

    public boolean isEDGE() { return isRIGHT() || isLEFT(); }
    public boolean isRIGHT() { return EDGE.equals("RIGHT"); }
    public boolean isLEFT() { return EDGE.equals("LEFT"); }
    public FixtureDef getFix() { return defFix; }
    public Vector2 getPosition() { return POS; }

}
