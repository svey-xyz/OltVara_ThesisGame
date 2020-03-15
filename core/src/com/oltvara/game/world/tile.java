package com.oltvara.game.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.oltvara.game.gamestates.play;
import com.oltvara.game.world.wrldHandlers.chunk;
import com.oltvara.game.world.wrldHandlers.physicsVars;
import com.oltvara.game.mainGame;

import static com.oltvara.game.world.wrldHandlers.physicsVars.PPM;
import static com.oltvara.game.mainGame.fct;

public class tile {

    private final float size;
    public TextureRegion tx;
    public String[] grassNames, groundNames, liveGroundNames, rockNames, liveDGroundNames;
    BodyDef defBod;
    Body bod;
    private FixtureDef defFix;
    private final Vector2 POS;
    private final int OFFSET;
    private double rand;
    private int txPick;
    private int xFLIP, yFLIP;
    private final String EDGE;

    public tile(Vector2 pos, int offset, String edge) {
        this.POS = pos;
        this.OFFSET = offset;
        this.EDGE = edge;

        grassNames = new String[]{"grass-1-1", "grass-1-2", "grass-1-3"};
        groundNames = new String[]{"ground-1-1", "ground-1-2", "groundSpeckle-1-1", "groundSpeckle-1-2"};
        rockNames = new String[]{"groundRock-1-1", "groundRockSpeckle-1-1"};
        liveGroundNames = new String[]{"groundLive-1-1", "groundLiveSpeckle-1-1"};
        liveDGroundNames = new String[]{"groundDLive-1-1", "groundDLiveSpeckle-1-1"};

        size = mainGame.TILESIZE;

        defBod = new BodyDef();

        defBod.type = BodyDef.BodyType.StaticBody;
        defBod.position.set((pos.x + 0.5f + OFFSET) * size / PPM, (pos.y + 0.5f) * size / PPM);
        defFix  = new FixtureDef();

        bod = play.boxWorld.createBody(defBod);
        bod.createFixture(createFix());
    }

    public void updateTexture(String[] txChoice, int flip) {
        this.xFLIP = flip;
        rand = fct.random();
        if (txChoice.equals(groundNames)) {
            if (rand < 0.1) {
                txPick = (int) mainGame.fct.random(0, txChoice.length - 1);
                tx = mainGame.groundAtlas.findRegion(txChoice[txPick]);
            } else {
                txPick = (int) mainGame.fct.random(0, txChoice.length - 1);
                tx = mainGame.groundAtlas.findRegion(txChoice[txPick]);
            }
        } else {
            txPick = (int) mainGame.fct.random(0, txChoice.length - 1);
            tx = mainGame.groundAtlas.findRegion(txChoice[txPick]);
        }
    }

    public void render(SpriteBatch sb) {
        if (tx == null) { return; }
        sb.begin();
        sb.draw(
                tx,
                (defBod.position.x) * physicsVars.PPM - size / 2,
                (defBod.position.y) * physicsVars.PPM - size / 2
        );
        sb.end();
    }

    private FixtureDef createFix() {

        PolygonShape box = new PolygonShape();

        ChainShape cs = new ChainShape();
        Vector2[] v = new Vector2[4];

        v[0] = new Vector2(-mainGame.TILESIZE / 2 / PPM, -mainGame.TILESIZE / 2 /PPM);
        v[1] = new Vector2(-mainGame.TILESIZE / 2 / PPM, mainGame.TILESIZE / 2 /PPM);
        v[2] = new Vector2(mainGame.TILESIZE / 2 / PPM, mainGame.TILESIZE / 2 /PPM);
        v[3] = new Vector2(mainGame.TILESIZE / 2 / PPM, -mainGame.TILESIZE / 2 /PPM);

        cs.createChain(v);
        defFix.shape = cs;

        defFix.friction = 0;
        defFix.filter.categoryBits = physicsVars.bitGROUND;
        defFix.filter.maskBits = -1;
        defFix.isSensor = false;

        return defFix;
    }

    public boolean isEDGE() { return isRIGHT() || isLEFT(); }
    public boolean isRIGHT() { return EDGE.equals("RIGHT"); }
    public boolean isLEFT() { return EDGE.equals("LEFT"); }
    public Body getBod() { return bod; }
    public FixtureDef getFix() { return defFix; }
    public Vector2 getPosition() { return POS; }

}
