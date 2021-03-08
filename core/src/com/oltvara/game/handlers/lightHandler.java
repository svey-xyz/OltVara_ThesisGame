package com.oltvara.game.handlers;

import box2dLight.BlendFunc;
import box2dLight.DirectionalLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.oltvara.game.gamestates.play;

import java.util.HashMap;

import static com.oltvara.game.gamestates.play.boxWorld;
import static com.oltvara.game.mainGame.*;
import static com.oltvara.game.handlers.physicsVars.PPM;

public class lightHandler {

    private ShaderProgram shadowShader, godRayShader, occlusionShader;
    private int multipleLights;
    private Mesh quad;
    private FrameBuffer fbo;
    private Texture occlusionLayer;

    private RayHandler frontLights, mainLights, backLights, charLights;
    private HashMap<String, RayHandler> rayHandlers;

    private final BlendFunc shadowRayBlend = new BlendFunc(Gdx.gl20.GL_SRC_COLOR, Gdx.gl20.GL_ONE_MINUS_SRC_ALPHA);

    public lightHandler() {
        RayHandler.useDiffuseLight(true);

        rayHandlers = new HashMap<>();

        initHandlers();
        addLights();

        shadowShader = setupShader("shadowShader");
        godRayShader = setupShader("godRayShader");
        occlusionShader = setupShader("occlusionShader");

        quad = creatQuad();
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, (cWIDTH * SCALE), (cHEIGHT * SCALE), false);
    }

    public Texture createOcclude(Texture scene, Texture light, int drawLights) {
        this.occlusionLayer = scene;

        fbo.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);
        occlusionLayer.bind();

        //bind the lightmap texture
        Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE1);
        light.bind();

        Gdx.gl20.glEnable(Gdx.gl20.GL_BLEND);
        Gdx.gl20.glBlendFunc(Gdx.gl20.GL_SRC_ALPHA, Gdx.gl20.GL_ONE);

        occlusionShader.begin();
        {
            occlusionShader.setUniformi("u_texture0", 0);
            occlusionShader.setUniformi("u_texture1", 1);
            occlusionShader.setUniformi("renderLight", drawLights);
            quad.render(occlusionShader, GL20.GL_TRIANGLE_FAN, 0, 4);
        }
        occlusionShader.end();

        fbo.end();

        Gdx.gl20.glDisable(Gdx.gl20.GL_BLEND);
        Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);

        return fbo.getColorBufferTexture();
    }

    public void blendLights(Texture tex0, Texture tex1, Texture tex2, float charLightIntensity, Color ambientCol, Color shadowCol) {
        Gdx.gl20.glBlendEquation(Gdx.gl20.GL_FUNC_ADD);
        //bind the world texture
        Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);
        tex0.bind();

        //bind the lightmap texture
        Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE1);
        tex1.bind();

        //bind the second lightmap texture
        Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE2);
        if (tex2 != null) {
            tex2.bind();
            multipleLights = 1;
        } else {
            multipleLights = 0;
        }

        //blend the two textures- happens in the fragment shader
        Gdx.gl20.glEnable(Gdx.gl20.GL_BLEND);
        Gdx.gl20.glBlendFunc(Gdx.gl20.GL_SRC_ALPHA, Gdx.gl20.GL_ONE_MINUS_SRC_ALPHA);

        shadowShader.begin();
        {
            shadowShader.setUniformf("ambient_color", ambientCol);
            shadowShader.setUniformf("shadow_color", shadowCol);
            shadowShader.setUniformi("u_texture0", 0);
            shadowShader.setUniformi("u_texture1", 1);
            shadowShader.setUniformi("u_texture2", 2);
            shadowShader.setUniformi("mulLights", multipleLights);
            shadowShader.setUniformf("intensity", charLightIntensity);
            quad.render(shadowShader, GL20.GL_TRIANGLE_FAN, 0, 4);
        }
        shadowShader.end();

        //return things to normal
        Gdx.gl20.glDisable(Gdx.gl20.GL_BLEND);
        Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);
    }

    public void renderGodRays(Texture scene, Vector2 lightPos, boolean light, float exposure, float decay, float density, float weight) {
        Gdx.gl20.glEnable(Gdx.gl20.GL_BLEND);
        Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);
        scene.bind();
        if (!light) Gdx.gl20.glBlendFunc(Gdx.gl20.GL_SRC_COLOR, Gdx.gl20.GL_ONE_MINUS_SRC_ALPHA);
        else Gdx.gl20.glBlendFunc(Gdx.gl20.GL_SRC_COLOR, Gdx.gl20.GL_ONE);
        //Gdx.gl20.glBlendFunc(Gdx.gl20.GL_SRC_ALPHA, Gdx.gl20.GL_ONE_MINUS_SRC_ALPHA);

        godRayShader.begin();
        {
            godRayShader.setUniformf("exposure", exposure);
            godRayShader.setUniformf("decay", decay);
            godRayShader.setUniformf("density", density);
            godRayShader.setUniformf("weight", weight);
            godRayShader.setUniformf("lightPositionOnScreen", lightPos);
            quad.render(godRayShader, GL20.GL_TRIANGLE_FAN, 0, 4);
        }
        godRayShader.end();

        //return things to normal
        Gdx.gl20.glDisable(Gdx.gl20.GL_BLEND);
    }

    private void initHandlers() {
        frontLights = new RayHandler(boxWorld);
        frontLights.setBlur(true);
        frontLights.setCulling(true);
        rayHandlers.put("frontLights", frontLights);

        mainLights = new RayHandler(boxWorld);
        mainLights.setBlur(true);
        mainLights.resizeFBO(100, 100);
        mainLights.setCulling(true);
        rayHandlers.put("mainLights", mainLights);

        backLights = new RayHandler(boxWorld);
        backLights.setBlur(true);
        backLights.setAmbientLight(0.5f);
        backLights.setCulling(true);
        rayHandlers.put("backLights", backLights);

        charLights = new RayHandler(boxWorld);
        charLights.setBlur(true);
        charLights.setCulling(true);
        rayHandlers.put("charLights", charLights);
    }

    private void addLights() {
        Filter filter = new Filter();
        filter.categoryBits = physicsVars.bitLIGHT;
        filter.maskBits = physicsVars.bitTREE;

        //Character light
        PointLight charLight = new PointLight(charLights, 360, Color.WHITE, 128 / PPM, 0, 0);
        charLight.setColor(1, 0.9f, 0.9f, 1);
        charLight.setSoftnessLength(0.25f);
        charLight.setSoft(true);
        charLight.attachToBody(play.getChar().getBod());
        charLight.setIgnoreAttachedBody(true);
        charLight.setContactFilter(filter);
        //charLight.add(backLights);
        //charLight.add(mainLights);

        /*WORLD LIGHTS*/
        filter.categoryBits = physicsVars.bitCHAR;
        filter.maskBits = physicsVars.bitGROUND;

        //main lights affect tiles and front bushes
        DirectionalLight mDirect = new DirectionalLight(mainLights, 200, Color.WHITE, -91);
        mDirect.setSoftnessLength(1f);
        mDirect.setContactFilter(filter);

        //front lights affect front trees
        DirectionalLight fDirect = new DirectionalLight(frontLights, 100, fct.fromRGB(100, 100, 100), -91);
        fDirect.setSoftnessLength(1.5f);
        fDirect.setContactFilter(filter);

        //back lights affect back trees and back bushes
        DirectionalLight bDirect = new DirectionalLight(backLights, 100, fct.fromRGB(225, 225, 225), -91);
        bDirect.setSoftnessLength(1.5f);
        bDirect.setContactFilter(filter);
    }

    //create shader program from shader files
    private ShaderProgram setupShader(final String prefix) {
        ShaderProgram.pedantic = false;
        final ShaderProgram shaderProgram = new ShaderProgram(Gdx.files.internal("shaders/" + prefix + "_v.glsl"),
                Gdx.files.internal("shaders/" + prefix + "_f.glsl"));

        if (!shaderProgram.isCompiled()) {
            System.err.println("Error with shader " + prefix + ": " + shaderProgram.getLog());
            System.exit(1);
        } else {
            Gdx.app.log("init", "Shader " + prefix + " compilled " + shaderProgram.getLog());
        }
        return shaderProgram;
    }

    //for rendering the lightmaps
    private Mesh creatQuad() {
        //define the vertices for a full screen quad
        float[] verts = new float[20];
        int i = 0;

        verts[i++] = -1; // x1
        verts[i++] = -1; // y1
        verts[i++] = 0;
        verts[i++] = 0f; // u1
        verts[i++] = 0f; // v1

        verts[i++] = 1f; // x2
        verts[i++] = -1; // y2
        verts[i++] = 0;
        verts[i++] = 1f; // u2
        verts[i++] = 0f; // v2

        verts[i++] = 1f; // x3
        verts[i++] = 1f; // y2
        verts[i++] = 0;
        verts[i++] = 1f; // u3
        verts[i++] = 1f; // v3

        verts[i++] = -1; // x4
        verts[i++] = 1f; // y4
        verts[i++] = 0;
        verts[i++] = 0f; // u4
        verts[i++] = 1f; // v4

        Mesh quad = new Mesh(true, 4, 0,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE));
        quad.setVertices(verts);

        return quad;
    }

    public RayHandler getRayHandler(String name) {
        return rayHandlers.get(name);
    }
}
