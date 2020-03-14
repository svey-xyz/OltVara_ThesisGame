package com.oltvara.game.gamestates;

import static com.oltvara.game.world.wrldHandlers.physicsVars.PPM;
import static com.oltvara.game.mainGame.fct;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.oltvara.game.entities.mainChar;
import com.oltvara.game.world.map;
import com.oltvara.game.world.wrldHandlers.contactListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.oltvara.game.handlers.inputControl;
import com.oltvara.game.world.wrldHandlers.physicsVars;
import com.oltvara.game.handlers.stateHandler;
import com.oltvara.game.mainGame;
import com.oltvara.game.world.tile;
import com.oltvara.game.world.wrldHandlers.chunk;

public class play extends gameState{

    private final boolean DEBUG = false;

    public static World boxWorld;
    private Box2DDebugRenderer bdRen;
    private Vector3 targetPos;
    private final float CAMSPEEDMODIFER = 2f;
    private float yDiff, dist;

    private OrthographicCamera boxCam;

    //for loaded Tile maps
    private TiledMap tileMap;
    private float tileSize;
    private OrthogonalTiledMapRenderer tmr;

    //for random map generation
    private map mapControl;
    private int lenPower2;
    private float camRightPoint, camLeftPoint;
    private int len;
    private int cOffset = 0;
    private int rOffset, lOffset;
    private float leftPoint, rightPoint;

    private static Array<Body> bodiesToRemove;

    //Character physics stuff
    private contactListener cl;
    private mainChar myChar;
    private Body charBod;
    private final float MAXSPEED = 1.5f;
    private final float ACC = 0.1f;
    private final int JUMP = 100;
    private float jump;
    private Vector2 pos, vel;
    private int holdTime;



    public play(stateHandler GSH) {
        super(GSH);

        bodiesToRemove = new Array<Body>();

        len = (int)(mainGame.cWIDTH / mainGame.TILESIZE);

        lenPower2 = (int)Math.pow(2, Math.ceil(Math.log(len) / (Math.log(2))));

        //setup physics stuff
        boxWorld = new World(new Vector2(0, -9.81f), true);
        cl = new contactListener();
        boxWorld.setContactListener(cl);
        bdRen = new Box2DDebugRenderer();

        //setup debug Cam
        boxCam = new OrthographicCamera();
        boxCam.setToOrtho(false, mainGame.cWIDTH / PPM, mainGame.cHEIGHT / PPM);

        //static - unaffected; dynamic - affected; kinematic - unaffected but can still move
        //create player
        createChar();

        //map generation init
        mapControl = new map(20);
        mapControl.addChunk(cOffset);
    }

    public  void handleInput() {
        charBod = myChar.getBod();
        pos = charBod.getPosition();
        vel = charBod.getLinearVelocity();

        if (inputControl.isReleased(inputControl.JUMPBUT) && cl.isCharContact()) {
            holdTime = inputControl.heldTime(inputControl.JUMPBUT);
            jump = JUMP * (holdTime / 2f);
            jump = (int)fct.constrain(jump, JUMP, 200);

            charBod.applyForceToCenter(0, jump, true);
        }

        if (inputControl.isPressed(inputControl.RIGHT) && vel.x < MAXSPEED) {
            if (cl.isCharContact()) {
                charBod.applyLinearImpulse(ACC, 0f, pos.x, pos.y, true);
            } else {
                charBod.applyLinearImpulse(ACC/2, 0f, pos.x, pos.y, true);
            }
        }

        if (inputControl.isPressed(inputControl.LEFT) && vel.x > -MAXSPEED) {
            if (cl.isCharContact()) {
                charBod.applyLinearImpulse(-ACC, 0f, pos.x, pos.y, true);
            } else {
                charBod.applyLinearImpulse(-ACC/2, 0f, pos.x, pos.y, true);
            }
        }

        if (!inputControl.isPressed(inputControl.LEFT) && !inputControl.isPressed(inputControl.RIGHT)) {
            charBod.applyLinearImpulse(-(vel.x / 4), 0f, pos.x, pos.y, true);
        }
    }

    public  void update(float delta) {

        handleInput();

        boxWorld.step(delta, 6, 2);

        //remove obejcts after world has finished updating
        for (int i = 0; i < bodiesToRemove.size; i++) {
            Body b = bodiesToRemove.get(i);
            //remove from array
            boxWorld.destroyBody(b);
        }
        bodiesToRemove.clear();

        myChar.update(delta);
    }

    public  void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        yDiff = Math.abs(mainCam.position.y - pos.y * PPM);

        if (yDiff > mainGame.cHEIGHT / 8f) {
            targetPos = new Vector3(pos.x * PPM, pos.y * PPM, mainCam.position.z);
        } else {
            targetPos = new Vector3(pos.x * PPM, mainCam.position.y, mainCam.position.z);
        }

        dist = fct.distance(mainCam.position.x, mainCam.position.y, targetPos.x, targetPos.y);

        targetPos.y = (float)fct.constrain(targetPos.y, mainGame.cHEIGHT / 2f, 1000000000f);

        if (dist < 1) {
            mainCam.position.set(targetPos);
        } else {
            mainCam.position.lerp(targetPos, (CAMSPEEDMODIFER / dist));
        }

        mainCam.update();

        camRightPoint = mainCam.position.x + mainCam.viewportWidth / 2;
        camLeftPoint = mainCam.position.x - mainCam.viewportWidth / 2;

        rightPoint = lenPower2 * mainGame.TILESIZE * (cOffset + 1);
        leftPoint = mainGame.TILESIZE + lenPower2 * mainGame.TILESIZE * cOffset;

        if (myChar.getPosition().x * PPM > rightPoint + mainCam.viewportWidth / 2) {
           mapControl.removeChunk(cOffset - 1);
           cOffset++;
        }
        if (myChar.getPosition().x * PPM < leftPoint - mainCam.viewportWidth / 2) {
            mapControl.removeChunk(cOffset + 1);
            cOffset--;
        }

        if (camRightPoint > rightPoint && !mapControl.hasChunk(cOffset + 1)) {
            mapControl.addChunk(cOffset + 1);
        }

        if (camLeftPoint < leftPoint && !mapControl.hasChunk(cOffset - 1)) {
            mapControl.addChunk(cOffset - 1);
        }

        //System.out.println();

        //for drawing loaded maps
        //tmr.setView(mainCam);
        //tmr.render();

        batch.setProjectionMatrix(mainCam.combined);
        mapControl.render(batch);
        myChar.render(batch);

        if (DEBUG) {
            boxCam.position.set(mainCam.position).scl(1/PPM);
            boxCam.update();
            bdRen.render(boxWorld, boxCam.combined);

        }
    }

    public static void addBodToDestroy(Body b) {
        bodiesToRemove.add(b);
    }

    //create main character
    private void createChar() {
        BodyDef defBod = new BodyDef();
        PolygonShape box = new PolygonShape();
        FixtureDef defFix = new FixtureDef();

        //create main Character
        defBod.position.set(160 / PPM, 200 / PPM);
        defBod.type = BodyDef.BodyType.DynamicBody;
        Body body= boxWorld.createBody(defBod);

        box.setAsBox(8 / PPM,31 / PPM);
        defFix.shape = box;
        defFix.filter.categoryBits = physicsVars.bitCHAR;
        defFix.filter.maskBits = physicsVars.bitGROUND;
        body.createFixture(defFix).setUserData("mainChar");

        //create foot sensor
        box.setAsBox(8 / PPM, 2 / PPM, new Vector2(0, -31 / PPM), 0);
        defFix.shape = box;
        defFix.filter.categoryBits = physicsVars.bitCHAR;
        defFix.filter.maskBits = physicsVars.bitGROUND;
        defFix.isSensor = true;
        body.createFixture(defFix).setUserData("sensor");

        body.setUserData(myChar);
        myChar = new mainChar(body);
    }

    //for loading premade maps
    private void createTiles() {
        BodyDef defBod = new BodyDef();
        FixtureDef defFix = new FixtureDef();

        //load tiled map
        tileMap = new TmxMapLoader().load("resources/tiles/mapTest.tmx");
        tmr = new OrthogonalTiledMapRenderer(tileMap);

        TiledMapTileLayer layer = (TiledMapTileLayer) tileMap.getLayers().get("Ground");

        tileSize = layer.getTileWidth();

        for (int row=0; row < layer.getHeight(); row++) {
            for (int col = 0; col < layer.getWidth(); col++) {
                TiledMapTileLayer.Cell cell = layer.getCell(col, row);

                if (cell == null) continue;
                if (cell.getTile() == null) continue;

                //create physics box
                defBod.type = BodyDef.BodyType.StaticBody;
                defBod.position.set((col + 0.5f) * tileSize / PPM, (row + 0.5f) * tileSize / PPM);

                ChainShape cs = new ChainShape();
                Vector2[] v = new Vector2[4];

                v[0] = new Vector2(-tileSize / 2 / PPM, -tileSize / 2 /PPM);
                v[1] = new Vector2(-tileSize / 2 / PPM, tileSize / 2 /PPM);
                v[2] = new Vector2(tileSize / 2 / PPM, tileSize / 2 /PPM);
                v[3] = new Vector2(tileSize / 2 / PPM, -tileSize / 2 /PPM);

                cs.createChain(v);
                defFix.shape = cs;
                defFix.filter.categoryBits = physicsVars.bitGROUND;
                defFix.filter.maskBits = -1;
                defFix.isSensor = false;

                boxWorld.createBody(defBod).createFixture(defFix);
            }
        }
    }

    public  void dispose() {}

}
