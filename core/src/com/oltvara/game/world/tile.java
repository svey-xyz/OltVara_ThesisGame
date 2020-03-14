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

public class tile {

    private final float size;
    private TextureRegion tx;
    private String[] grassNames, groundNames, liveGroundNames;
    BodyDef defBod;
    Body bod;
    private FixtureDef defFix;
    private Vector2 pos;
    private final int OFFSET;

    public tile(Vector2 pos, chunk ch) {
        this.pos = pos;
        this.OFFSET = ch.getOFFSET();

        grassNames = new String[]{"grass-1-1", "grass-1-2", "grass-1-3"};
        groundNames = new String[]{"ground-1-1", "ground-1-2", "groundRock-1-1", "groundRockSpeckle-1-1", "groundSpeckle-1-1", "groundSpeckle-1-2"};
        liveGroundNames = new String[]{"groundLive-1-1", "groundLiveSpeckle-1-1"};

        int txPick = (int)mainGame.fct.random(0, groundNames.length - 1);

        tx = mainGame.groundAtlas.findRegion(groundNames[txPick]);
        size = mainGame.TILESIZE;

        defBod = new BodyDef();

        defBod.type = BodyDef.BodyType.StaticBody;
        defBod.position.set((pos.x + 0.5f + OFFSET) * size / PPM, (pos.y + 0.5f) * size / PPM);
        defFix  = new FixtureDef();

        bod = play.boxWorld.createBody(defBod);
        bod.createFixture(createFix());
    }

    public void render(SpriteBatch sb) {
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

    public Body getBod() { return bod; }
    public FixtureDef getFix() { return defFix; }
    public Vector2 getPosition() { return pos; }

}
