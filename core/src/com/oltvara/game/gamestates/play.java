package com.oltvara.game.gamestates;

import static com.oltvara.game.mainGame.*;
import static com.oltvara.game.world.wrldHandlers.physicsVars.PPM;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.oltvara.game.entities.mainChar;
import com.oltvara.game.world.map;
import com.oltvara.game.world.tree;
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


public class play extends gameState{

    private final boolean DEBUG = true;

    public static World boxWorld;
    private Box2DDebugRenderer bdRen;
    private Vector3 targetPos;
    private final float CAMSPEEDMODIFER = 300f;
    private float camLastY;
    private float yDiff, dist;

    private OrthographicCamera boxCam;

    //for loaded Tile maps
    private TiledMap tileMap;
    private OrthogonalTiledMapRenderer tmr;

    public static map getMapControl() {
        return mapControl;
    }

    //for random map generation
    private static map mapControl;
    private float camRightPoint, camLeftPoint;
    private int cOffset = 0, lOffset =0;
    private int leftPoint, rightPoint, leftPointTotal, rightPointTotal;

    private static Array<Body> bodiesToRemove;

    //Character physics stuff
    private contactListener cl;
    private mainChar myChar;
    private tree myTree;
    private Body treeBod;
    private Body charBod;
    private final float MAXSPEED = 1.5f;
    private final float ACC = 0.1f;
    private final int JUMP = 50;
    private final int MAXJUMPVEL = 3;
    private float jump;
    private Vector2 pos, vel;
    private int holdTime;
    private boolean jumping = false;

    private boolean movingY;



    public play(stateHandler GSH) {
        super(GSH);

        bodiesToRemove = new Array<Body>();

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
        createTree();
        createChar();

        charBod = myChar.getBod();
        pos = charBod.getPosition();
        mainCam.position.set(new Vector3(pos.x * PPM, pos.y * PPM, mainCam.position.z));

        //map generation init
        mapControl = new map(30);
        mapControl.addChunk(cOffset);
    }

    public void handleInput() {
        charBod = myChar.getBod();
        pos = charBod.getPosition();
        vel = charBod.getLinearVelocity();

        if (inputControl.isTap(inputControl.JUMPBUT) && cl.isCharContact()) {
            jumping = true;
        }

        if (inputControl.isPressed(inputControl.JUMPBUT) && jumping) {
            if (charBod.getLinearVelocity().y < MAXJUMPVEL) {
                holdTime = inputControl.heldTime(inputControl.JUMPBUT);
                jump = JUMP * (holdTime / 4f);
                jump = (int) fct.constrain(jump, JUMP, 200);

                System.out.println(charBod.getLinearVelocity().y);

                charBod.applyForceToCenter(0, jump, true);
            } else {
                jumping = false;
            }
        }

        if (inputControl.isReleased(inputControl.JUMPBUT)) {
            jumping = false;
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

    public void update(float delta) {

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
        myTree.update(delta);
        createChunks();
    }

    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cameraMovement();

        //System.out.println();

        //for drawing loaded maps
        //tmr.setView(mainCam);
        //tmr.render();

        batch.setProjectionMatrix(mainCam.combined);
        mapControl.render(batch);
        myChar.render(batch, Color.WHITE);
        myTree.render(batch);

        if (DEBUG) {
            boxCam.position.set(mainCam.position).scl(1/PPM);
            boxCam.update();
            bdRen.render(boxWorld, boxCam.combined);

        }
    }

    private void cameraMovement() {
        yDiff = Math.abs(mainCam.position.y - pos.y * PPM);

        //controlled Y movement
        if (yDiff > cHEIGHT / 4f) { movingY = true; }
        if (yDiff < TILESIZE / 4f) { movingY = false; }

        //Set target pose based on yDiff
        if (movingY) {
            targetPos = new Vector3(pos.x * PPM, pos.y * PPM, mainCam.position.z);
        }
        if (!movingY) {
            targetPos = new Vector3(pos.x * PPM, mainCam.position.y, mainCam.position.z);
        }

        //distance between camera and character position determines lerp speed
        dist = fct.distance(mainCam.position.x, mainCam.position.y, targetPos.x, targetPos.y);

        //don't go below bottom block
        targetPos.y = (float)fct.constrain(targetPos.y, cHEIGHT / 2f, 1000000000f);

        //lerping
        final float speed=(dist/CAMSPEEDMODIFER), ySpeed = speed/ 10, ispeed=1.0f-speed, yIsSpeed=1.0f-ySpeed;
        Vector3 cameraPosition = mainCam.position;
        cameraPosition.scl(ispeed, yIsSpeed, 1);
        targetPos.scl(speed, ySpeed, 1);
        cameraPosition.add(targetPos);

        //cameraPosition.set(fct.roundVec(cameraPosition));

        mainCam.position.set(cameraPosition);
        mainCam.update();
    }

    public static void addBodToDestroy(Body b) {
        bodiesToRemove.add(b);
    }

    private void createChunks() {
        if (lOffset != cOffset) {
            System.out.println(cOffset);
            lOffset = cOffset;
        }

        camRightPoint = (mainCam.position.x + mainCam.viewportWidth / 2);
        camLeftPoint = mainCam.position.x - mainCam.viewportWidth / 2;

        rightPoint = (numTILES - tileBUFFER) * TILESIZE * (cOffset + 1);
        leftPoint = tileBUFFER * TILESIZE + numTILES * TILESIZE * cOffset;

        rightPointTotal = numTILES * TILESIZE * (cOffset + 1);
        leftPointTotal = numTILES * TILESIZE * cOffset;

        /*rightPoint = lenPower2 * mainGame.TILESIZE + (cOffset + 1);
        leftPoint = mainGame.TILESIZE + lenPower2 * mainGame.TILESIZE * cOffset;*/

        if (mainCam.position.x > rightPointTotal + mainCam.viewportWidth / 2) {
            mapControl.removeChunk(cOffset - 1);
            cOffset++;

        }
        if (mainCam.position.x < leftPointTotal - mainCam.viewportWidth / 2) {
            mapControl.removeChunk(cOffset + 1);
            cOffset--;
        }

        if (camRightPoint > rightPointTotal - (TILESIZE * tileBUFFER / 2f) && !mapControl.hasChunk(cOffset + 1)) {
            mapControl.addChunk(cOffset + 1);
        }

        if (camLeftPoint < leftPointTotal + (TILESIZE * tileBUFFER / 2f) && !mapControl.hasChunk(cOffset - 1)) {
            mapControl.addChunk(cOffset - 1);
        }
    }

    //create main character
    private void createChar() {
        BodyDef defBod = new BodyDef();
        PolygonShape box = new PolygonShape();
        FixtureDef defFix = new FixtureDef();

        //create main Character
        defBod.position.set(0, 1000 / PPM);
        defBod.type = BodyDef.BodyType.DynamicBody;
        Body body= boxWorld.createBody(defBod);

        box.setAsBox(8 / PPM,31 / PPM);
        defFix.shape = box;
        defFix.filter.categoryBits = physicsVars.bitCHAR;
        defFix.filter.maskBits = physicsVars.bitGROUND;
        body.createFixture(defFix).setUserData("mainChar");

        //create foot sensor
        box.setAsBox(7.5f / PPM, 2 / PPM, new Vector2(0, -31 / PPM), 0);
        defFix.shape = box;
        defFix.filter.categoryBits = physicsVars.bitCHAR;
        defFix.filter.maskBits = physicsVars.bitGROUND;
        defFix.isSensor = true;
        body.createFixture(defFix).setUserData("sensor");

        body.setUserData(myChar);
        myChar = new mainChar(body);
    }

    private void createTree() {
        myTree = new tree(new Vector2(0, 200 / PPM), "small_1-1-1", trTex.BUSHYTREE);
    }

    //for loading premade maps
    private void createTiles() {
        BodyDef defBod = new BodyDef();
        FixtureDef defFix = new FixtureDef();

        //load tiled map
        tileMap = new TmxMapLoader().load("resources/tiles/mapTest.tmx");
        tmr = new OrthogonalTiledMapRenderer(tileMap);

        TiledMapTileLayer layer = (TiledMapTileLayer) tileMap.getLayers().get("Ground");

        for (int row=0; row < layer.getHeight(); row++) {
            for (int col = 0; col < layer.getWidth(); col++) {
                TiledMapTileLayer.Cell cell = layer.getCell(col, row);

                if (cell == null) continue;
                if (cell.getTile() == null) continue;

                //create physics box
                defBod.type = BodyDef.BodyType.StaticBody;
                defBod.position.set((col + 0.5f) * TILESIZE / PPM, (row + 0.5f) * TILESIZE / PPM);

                ChainShape cs = new ChainShape();
                Vector2[] v = new Vector2[4];

                v[0] = new Vector2(-TILESIZE / 2f / PPM, -TILESIZE / 2f /PPM);
                v[1] = new Vector2(-TILESIZE / 2f / PPM, TILESIZE / 2f /PPM);
                v[2] = new Vector2(TILESIZE / 2f / PPM, TILESIZE / 2f /PPM);
                v[3] = new Vector2(TILESIZE / 2f / PPM, -TILESIZE / 2f /PPM);

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
