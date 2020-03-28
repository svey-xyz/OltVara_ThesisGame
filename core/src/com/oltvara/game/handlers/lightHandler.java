package com.oltvara.game.handlers;

import box2dLight.DirectionalLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.physics.box2d.Filter;
import com.oltvara.game.gamestates.play;
import com.oltvara.game.world.wrldHandlers.physicsVars;

import java.util.HashMap;

import static com.oltvara.game.gamestates.play.boxWorld;
import static com.oltvara.game.mainGame.*;
import static com.oltvara.game.world.wrldHandlers.physicsVars.PPM;

public class lightHandler {

    private RayHandler frontLights, mainLights, backLights;
    private HashMap<String, RayHandler> rayHandlers;

    public lightHandler() {
        RayHandler.useDiffuseLight(true);

        rayHandlers = new HashMap<>();

        initHandlers();
        addLights();
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
    }

    private void addLights() {
        Filter filter = new Filter();
        filter.categoryBits = physicsVars.bitLIGHT;
        filter.maskBits = physicsVars.bitTREE;

        //Character light
        PointLight charLight = new PointLight(mainLights, 200, Color.SCARLET, 128 / PPM, 0, 0);
        charLight.setColor(1, 1, 1, 2);
        charLight.setSoftnessLength(0.2f);
        charLight.setSoft(true);

        charLight.attachToBody(play.getChar().getBod());
        charLight.setIgnoreAttachedBody(true);
        charLight.setContactFilter(filter);
        charLight.add(backLights);


        /*WORLD LIGHTS*/
        filter.categoryBits = physicsVars.bitCHAR;
        filter.maskBits = physicsVars.bitGROUND;

        //main lights affect tiles and front bushes
        DirectionalLight mDirect = new DirectionalLight(mainLights, 200, Color.WHITE, -91);
        mDirect.setSoftnessLength(1.5f);
        mDirect.setContactFilter(filter);

        //front lights affect front trees
        DirectionalLight fDirect = new DirectionalLight(frontLights, 200, fct.fromRGB(80, 80, 80), -91);
        fDirect.setSoftnessLength(1.5f);
        fDirect.setContactFilter(filter);

        //back lights affect back trees and back bushes
        DirectionalLight bDirect = new DirectionalLight(backLights, 200, fct.fromRGB(175, 175, 175), -91);
        bDirect.setSoftnessLength(1.5f);
        bDirect.setContactFilter(filter);
    }

    //create shader program from shader files
    public ShaderProgram setupShader(final String prefix) {
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
    public Mesh creatQuad() {
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
