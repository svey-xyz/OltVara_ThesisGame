package com.oltvara.game.gamestates;

import static com.oltvara.game.mainGame.*;
import static com.oltvara.game.world.wrldHandlers.physicsVars.PPM;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
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


public class play extends gameState{

    private final boolean DEBUG = false;

    public static World boxWorld;
    private Box2DDebugRenderer bdRen;
    private Vector3 targetPos;
    private final float CAMSPEEDMODIFER = 300f;

    private OrthographicCamera boxCam;
    private OrthogonalTiledMapRenderer tmr;

    //for random map generation
    private static map mapControl;
    private float camRightPoint, camLeftPoint, camTopPoint, camBottomPoint;
    private int cOffset = 0, lOffset =0;
    private int leftPoint, rightPoint, leftPointTotal, rightPointTotal;
    private static Vector2 bottomLeftViewPoint;

    private static Array<Body> bodiesToRemove;

    //Character physics stuff
    private contactListener cl;
    private mainChar myChar;
    private Body charBod;
    private final float MAXSPEED = 1.5f;
    private final float ACC = 0.1f;
    private final int JUMP = 50;
    private final int MAXJUMPVEL = 3;
    private Vector2 pos;
    private boolean jumping = false;

    private boolean movingY;

    public play(stateHandler GSH) {
        super(GSH);

        bodiesToRemove = new Array<Body>();
        bottomLeftViewPoint = new Vector2();

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

        charBod = myChar.getBod();
        pos = charBod.getPosition();
        mainCam.position.set(new Vector3(pos.x * PPM, pos.y * PPM, mainCam.position.z));

        //map generation init
        mapControl = new map(25);
        mapControl.addChunk(cOffset);
    }

    public void handleInput() {
        charBod = myChar.getBod();
        pos = charBod.getPosition();
        Vector2 vel = charBod.getLinearVelocity();

        //set jump state
        if (inputControl.isTap(inputControl.JUMPBUT) && cl.isCharContact()) {
            jumping = true;
        }
        if (inputControl.isReleased(inputControl.JUMPBUT)) {
            jumping = false;
        }

        //Control jump height based on length button was pressed while still allowing jump on tap not on release
        if (inputControl.isPressed(inputControl.JUMPBUT) && jumping) {
            if (charBod.getLinearVelocity().y < MAXJUMPVEL) {
                int holdTime = inputControl.heldTime(inputControl.JUMPBUT);
                float jump = JUMP * (holdTime / 4f);
                jump = (int) fct.constrain(jump, JUMP, 200);

                charBod.applyForceToCenter(0, jump, true);
            } else {
                jumping = false;
            }
        }

        //apply force for movement
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

        //stop movement - better than friction
        if (!inputControl.isPressed(inputControl.LEFT) && !inputControl.isPressed(inputControl.RIGHT)) {
            charBod.applyLinearImpulse(-(vel.x / 4), 0f, pos.x, pos.y, true);
        }
    }

    //Might need to apply Euler's integration to avoid weird rendering issues with de-synchronization from physics update.
    private void cameraMovement() {
        float yDiff = Math.abs(mainCam.position.y - pos.y * PPM);

        //controlled Y movement
        if (yDiff > cHEIGHT / 4f) { movingY = true; }
        if (yDiff < TILESIZE / 4f) { movingY = false; }

        //Set target pos based on yDiff
        if (movingY) {
            targetPos = new Vector3(pos.x * PPM, pos.y * PPM + cHEIGHT / 6f, mainCam.position.z);
        }
        if (!movingY) {
            targetPos = new Vector3(pos.x * PPM, mainCam.position.y, mainCam.position.z);
        }

        //distance between camera and character position determines lerp speed
        float dist = fct.distance(mainCam.position.x, mainCam.position.y, targetPos.x, targetPos.y);

        //don't go below bottom block
        targetPos.y = (float)fct.constrain(targetPos.y, cHEIGHT / 2f, 1000000000f);

        //lerping
        final float speed=(dist /CAMSPEEDMODIFER), ySpeed = speed/ 10, ispeed=1.0f-speed, yIsSpeed=1.0f-ySpeed;
        Vector3 cameraPosition = mainCam.position;
        cameraPosition.scl(ispeed, yIsSpeed, 1);
        targetPos.scl(speed, ySpeed, 1);
        cameraPosition.add(targetPos);

        //cameraPosition.set(fct.roundVec(cameraPosition));

        mainCam.position.set(cameraPosition);
        mainCam.update();
    }

    public void update(float delta) {

        handleInput();

        boxWorld.step(delta, 6, 2);
        cameraMovement();

        //remove obejcts after world has finished updating
        for (int i = 0; i < bodiesToRemove.size; i++) {
            Body b = bodiesToRemove.get(i);
            //remove from array
            boxWorld.destroyBody(b);
        }
        bodiesToRemove.clear();

        mapControl.update(delta);
        myChar.update(delta);
        createChunks();
    }

    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //for drawing loaded maps
        /*tmr.setView(mainCam);
        tmr.render();*/

        batch.setProjectionMatrix(mainCam.combined);

        mapControl.renderBack(batch);
        mapControl.renderMain(batch);
        myChar.render(batch, Color.WHITE);
        mapControl.renderFront(batch);

        //For debugging box2D stuff.
        if (DEBUG) {
            boxCam.position.set(mainCam.position).scl(1/PPM);
            boxCam.update();
            bdRen.render(boxWorld, boxCam.combined);

        }
    }

    //for queuing physics bodies to destroy from other methods.
    public static void addBodToDestroy(Body b) {
        bodiesToRemove.add(b);
    }

    private void createChunks() {
        if (lOffset != cOffset) {
            System.out.println(cOffset);
            lOffset = cOffset;
        }

        //Figure out camera viewport compared to world units and furthest points of world in chunk
        camRightPoint = (mainCam.position.x + mainCam.viewportWidth / 2);
        camLeftPoint = mainCam.position.x - mainCam.viewportWidth / 2;

        rightPoint = (numTILES - tileBUFFER) * TILESIZE * (cOffset + 1);
        leftPoint = tileBUFFER * TILESIZE + numTILES * TILESIZE * cOffset;

        rightPointTotal = numTILES * TILESIZE * (cOffset + 1);
        leftPointTotal = numTILES * TILESIZE * cOffset;

        bottomLeftViewPoint.x = camLeftPoint;
        bottomLeftViewPoint.y = mainCam.position.y - mainCam.viewportHeight / 2;

        //remove chunks once they're out of view
        if (mainCam.position.x > rightPointTotal + mainCam.viewportWidth / 2) {
            mapControl.removeChunk(cOffset - 1);
            cOffset++;
        }
        if (mainCam.position.x < leftPointTotal - mainCam.viewportWidth / 2) {
            mapControl.removeChunk(cOffset + 1);
            cOffset--;
        }

        //add chunks just before they come into view- buffer can be adjusted in mainGame
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

        box.setAsBox(7 / PPM,30.5f / PPM);
        defFix.shape = box;
        defFix.filter.categoryBits = physicsVars.bitCHAR;
        defFix.filter.maskBits = physicsVars.bitGROUND;
        //defFix.restitution = 0.8f;
        body.createFixture(defFix).setUserData("mainChar");

        //create foot sensor
        box.setAsBox(6 / PPM, 0.5f / PPM, new Vector2(0.25f / PPM, -30 / PPM), 0);
        defFix.shape = box;
        defFix.filter.categoryBits = physicsVars.bitCHAR;
        defFix.filter.maskBits = physicsVars.bitGROUND;
        defFix.isSensor = true;
        body.createFixture(defFix).setUserData("sensor");

        body.setUserData(myChar);
        myChar = new mainChar(body);
    }

    //for loading pre-made maps
    private void createTiles() {
        BodyDef defBod = new BodyDef();
        FixtureDef defFix = new FixtureDef();

        //load tiled map
        //for loaded Tile maps
        TiledMap tileMap = new TmxMapLoader().load("resources/tiles/mapTest.tmx");
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

    public static Vector2 getBottomLeftViewPoint() {
        return bottomLeftViewPoint;
    }

    public static Vector2 getViewporSize() {
        return new Vector2(1, 1);
    }
}
