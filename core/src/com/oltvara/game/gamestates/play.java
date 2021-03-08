package com.oltvara.game.gamestates;

import static com.oltvara.game.mainGame.*;
import static com.oltvara.game.handlers.physicsVars.PPM;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.oltvara.game.entities.mainChar;
import com.oltvara.game.handlers.lightHandler;
import com.oltvara.game.world.map;
import com.oltvara.game.handlers.contactListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.oltvara.game.handlers.physicsVars;
import com.oltvara.game.handlers.stateHandler;
import com.oltvara.game.mainGame;

public class play extends gameState{

    private final boolean DEBUG = false;

    //rendering and world stuff
    public static World boxWorld;
    private Box2DDebugRenderer bdRen;
    private Vector3 targetPos;
    private boolean movingY;
    private final float CAMSPEEDMODIFER = 300f;
    private OrthographicCamera boxCam;
    private OrthogonalTiledMapRenderer tmr;

    //lighting stuff
    lightHandler lights;
    private RayHandler mainLights, backLights, frontLights, charLights;
    private Matrix4 lightRenderPos;
    private FrameBuffer fbo;
    private float charLightInt = 3f;
    private Texture sky, frontLayer, inBetweenLayer, backLayer;

    //for random map generation
    private map mapControl;
    private float camRightPoint, camLeftPoint;
    private int cOffset = 0, pOffset = 0;
    private int leftPointTotal, rightPointTotal;
    private static Vector2 bottomLeftViewPoint;

    private static Array<Body> bodiesToRemove;

    //Character physics stuff
    private static contactListener cl;
    private static mainChar myChar;
    private Vector2 charScreenPos;

    public play(stateHandler GSH) {
        super(GSH);

        //setup physics stuff
        boxWorld = new World(new Vector2(0, -9.81f), true);
        cl = new contactListener();
        boxWorld.setContactListener(cl);
        bdRen = new Box2DDebugRenderer();

        bodiesToRemove = new Array<Body>();
        bottomLeftViewPoint = new Vector2();

        //create character
        myChar = new mainChar(mainChar.createBody(boxWorld));
        mainCam.position.set(new Vector3(myChar.getPosition().x * PPM, myChar.getPosition().y * PPM, mainCam.position.z));
        charScreenPos = new Vector2(0.5f, 0.5f);

        //setup light stuff, has to be done after char creation since a light is attached to the char bod
        lights = new lightHandler();

        sky = frTex.createSky();

        frontLights = lights.getRayHandler("frontLights");
        mainLights = lights.getRayHandler("mainLights");
        backLights = lights.getRayHandler("backLights");
        charLights = lights.getRayHandler("charLights");

        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, (cWIDTH * SCALE), (cHEIGHT * SCALE), false);

        //setup debug Cam
        boxCam = new OrthographicCamera();
        boxCam.setToOrtho(false, mainGame.cWIDTH / PPM, mainGame.cHEIGHT / PPM);

        //map generation init
        mapControl = new map(25);
        mapControl.addChunk(cOffset);
    }

    public void handleInput(float delta) {
        myChar.charMovement(delta);
    }

    //Might need to apply Euler's integration to avoid weird rendering issues with de-synchronization from physics update.
    private void cameraMovement() {
        final float yDiff = Math.abs(mainCam.position.y - myChar.getPosition().y * PPM);

        //controlled Y movement - creates Mario style camera follow where camera only follows X unless yDiff is big enough
        //this is nice since the camera following every jump is annoying af
        //this is nice since the camera following every jump is annoying af
        if (yDiff > cHEIGHT / 4f) { movingY = true; }
        if (yDiff < TILESIZE / 4f) { movingY = false; }

        //Set target pos based on yDiff
        if (movingY) {
            targetPos = new Vector3(myChar.getPosition().x * PPM, myChar.getPosition().y * PPM + cHEIGHT / 6f, mainCam.position.z);
        }
        if (!movingY) {
            targetPos = new Vector3(myChar.getPosition().x * PPM, mainCam.position.y, mainCam.position.z);
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

        handleInput(delta);

        boxWorld.step(delta, 6, 2);
        frontLights.update();
        mainLights.update();
        backLights.update();
        charLights.update();
        cameraMovement();

        //remove obejcts after world has finished updating
        for (int i = 0; i < bodiesToRemove.size; i++) {
            Body b = bodiesToRemove.get(i);
            //remove from array
            boxWorld.destroyBody(b);
        }

        bodiesToRemove.clear();

        createChunks();

        mapControl.update(delta);
        myChar.update(delta);

        charScreenPos = new Vector2((myChar.getPosition().x * PPM) / (mainCam.position.x) / 2, (myChar.getPosition().y * PPM) / (mainCam.position.y) / 2);
        if (myChar.getPosition().x < 1f && myChar.getPosition().x > -1f) charScreenPos.x = 0.5f;
    }

    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //set viewpoints for lights and spritebatch to camera
        lightRenderPos = mainCam.combined.cpy().scl(PPM);

        frontLights.setCombinedMatrix(lightRenderPos);
        mainLights.setCombinedMatrix(lightRenderPos);
        backLights.setCombinedMatrix(lightRenderPos);
        charLights.setCombinedMatrix(lightRenderPos);

        batch.setProjectionMatrix(mainCam.combined);

        //render lights to be able to get their framebuffer
        mainLights.render();
        frontLights.render();
        backLights.render();
        charLights.render();

        //draw and blend the lights to the map layers
        lights.blendLights(sky, backLights.getLightMapTexture(), charLights.getLightMapTexture(), charLightInt / (charLightInt * 1.5f), Color.WHITE, Color.CLEAR);
        //lights.blendLights(drawFrameBuffer(mapControl.RENDERBACK), backLights.getLightMapTexture(), charLights.getLightMapTexture(), charLightInt);

        backLayer = drawFrameBuffer(mapControl.RENDERBACK);
        lights.renderGodRays(backLayer, new Vector2(0.5f, 0.5f), true,
                0.15f, 0.82f, 0.9f, 1.0f);
        lights.blendLights(backLayer, backLights.getLightMapTexture(), null, 0, Color.WHITE, Color.CLEAR);

        //draw the character inbetween layers
        batch.begin();
        myChar.render(batch, Color.WHITE);
        batch.end();

        lights.blendLights(drawFrameBuffer(mapControl.RENDERMAIN), mainLights.getLightMapTexture(), charLights.getLightMapTexture(), charLightInt, Color.WHITE, Color.BROWN);

        inBetweenLayer = drawFrameBuffer(mapControl.RENDERINBETWEEN);
        lights.renderGodRays(lights.createOcclude(inBetweenLayer, charLights.getLightMapTexture(), 0), charScreenPos, false,
                0.06f, 0.8f, 1.0f, 1.0f);
        lights.blendLights(inBetweenLayer, mainLights.getLightMapTexture(), charLights.getLightMapTexture(), charLightInt / (charLightInt * 2), Color.WHITE, Color.CLEAR);

        frontLayer = drawFrameBuffer(mapControl.RENDERFRONT);
        lights.renderGodRays(lights.createOcclude(frontLayer, charLights.getLightMapTexture(), 0), charScreenPos, false,
                0.065f, 0.78f, 1.0f, 1.0f);
        lights.blendLights(frontLayer, frontLights.getLightMapTexture(), null, 0, Color.WHITE, Color.CLEAR);


        //For debugging box2D stuff.
        if (DEBUG) {
            boxCam.position.set(mainCam.position).scl(1/PPM);
            boxCam.update();
            bdRen.render(boxWorld, boxCam.combined);
        }
    }

    private Texture drawFrameBuffer(int renderLayer) {
        Gdx.gl20.glBlendEquation(Gdx.gl20.GL_FUNC_ADD);

        //draw the map layer to a frame buffer
        fbo.begin();
        {
            //clear sprite batch
            Gdx.gl.glClearColor(0, 0, 0, 0f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            //for fixing weird blending with transparency and shaders
            //also flips render order use GL_ONE_MINUS_SRC_ALPHA to reverse
            batch.setBlendFunction(Gdx.gl20.GL_SRC_ALPHA_SATURATE, Gdx.gl20.GL_ONE);

            batch.enableBlending();
            batch.begin();
            mapControl.render(batch, renderLayer);
            batch.end();
        }
        fbo.end();

        //return things to normal
        batch.setBlendFunction(Gdx.gl20.GL_SRC_ALPHA, Gdx.gl20.GL_ONE_MINUS_SRC_ALPHA);

        return fbo.getColorBufferTexture();
    }

    //for queuing physics bodies to destroy from other methods.
    public static void addBodToDestroy(Body b) {
        bodiesToRemove.add(b);
    }

    private void createChunks() {
        if (pOffset != cOffset) {
            System.out.println("Chunk: " + cOffset);
            pOffset = cOffset;
        }

        //Figure out camera viewport compared to world units and furthest points of world in chunk
        //These variables are created on startup rather than in this method to save memory instead of creating them every render frame
        camRightPoint = (mainCam.position.x + mainCam.viewportWidth / 2);
        camLeftPoint = mainCam.position.x - mainCam.viewportWidth / 2;

        rightPointTotal = numTILES * TILESIZE * (cOffset + 1);
        leftPointTotal = numTILES * TILESIZE * cOffset;

        bottomLeftViewPoint.x = camLeftPoint;
        bottomLeftViewPoint.y = mainCam.position.y - mainCam.viewportHeight / 2;

        //System.out.println((myChar.getPosition().x * PPM) / (mainCam.position.x) / 2);
        //System.out.println((myChar.getPosition().y * PPM) / (mainCam.position.y) / 2);

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

    public  void dispose() {
        frontLights.dispose();
        bdRen.dispose();
        boxWorld.dispose();
    }

    public static Vector2 getBottomLeftViewPoint() {
        return bottomLeftViewPoint;
    }

    public static Vector2 getViewporSize() {
        return new Vector2(1, 1);
    }

    public static mainChar getChar() {
        return myChar;
    }

    public static contactListener getCL() {
        return cl;
    }
}
