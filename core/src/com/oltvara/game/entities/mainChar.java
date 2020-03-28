package com.oltvara.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.oltvara.game.gamestates.play;
import com.oltvara.game.handlers.inputControl;
import com.oltvara.game.mainGame;
import com.oltvara.game.world.wrldHandlers.contactListener;
import com.oltvara.game.world.wrldHandlers.physicsVars;

import static com.oltvara.game.mainGame.fct;
import static com.oltvara.game.world.wrldHandlers.physicsVars.PPM;

public class mainChar extends sprite {

    private contactListener cl;
    private final float MAXSPEED = 1.5f;
    private final float ACC = 2f;
    private final int JUMP = 50;
    private final int MAXJUMPVEL = 3;
    private Vector2 pos;
    private boolean jumping = false;

    public mainChar(Body body) {
        super(body);
        body.setUserData(this);

        cl = play.getCL();

        Texture tex = mainGame.src.getTX("mainChar");
        TextureRegion[] sprites = TextureRegion.split(tex, 64, 64)[0];
        setAnim(sprites, 1 / 12f, 0);
    }

    public void charMovement(float delta) {
        pos = body.getPosition();
        Vector2 vel = body.getLinearVelocity();

        //set jump state
        if (inputControl.isTap(inputControl.JUMPBUT) && cl.isCharContact()) {
            jumping = true;
        }
        if (inputControl.isReleased(inputControl.JUMPBUT)) {
            jumping = false;
        }

        //Control jump height based on length button was pressed while still allowing jump on tap not on release
        if (inputControl.isPressed(inputControl.JUMPBUT) && jumping) {
            if (body.getLinearVelocity().y < MAXJUMPVEL) {
                int holdTime = inputControl.heldTime(inputControl.JUMPBUT);
                float jump = JUMP * (holdTime / 5f) * delta;
                jump = (int) fct.constrain(jump, JUMP, 200);

                body.applyForceToCenter(0, jump, true);
            } else {
                jumping = false;
            }
        }

        //apply force for movement
        if (inputControl.isPressed(inputControl.RIGHT) && vel.x < MAXSPEED) {
            if (cl.isCharContact()) {
                body.applyLinearImpulse(ACC * delta, 0f, pos.x, pos.y, true);
            } else {
                body.applyLinearImpulse(ACC/2 * delta, 0f, pos.x, pos.y, true);
            }
        }
        if (inputControl.isPressed(inputControl.LEFT) && vel.x > -MAXSPEED) {
            if (cl.isCharContact()) {
                body.applyLinearImpulse(-ACC * delta, 0f, pos.x, pos.y, true);
            } else {
                body.applyLinearImpulse(-ACC/2 * delta, 0f, pos.x, pos.y, true);
            }
        }

        //stop movement - better than friction
        if (!inputControl.isPressed(inputControl.LEFT) && !inputControl.isPressed(inputControl.RIGHT)) {
            body.applyLinearImpulse(-(vel.x * ACC * 3 * delta), 0f, pos.x, pos.y, true);
        }
    }

    public static Body createBody(World wrld) {
        BodyDef defBod = new BodyDef();
        PolygonShape box = new PolygonShape();
        FixtureDef defFix = new FixtureDef();

        //create main Character
        defBod.position.set(0, 1000 / PPM);
        defBod.type = BodyDef.BodyType.DynamicBody;
        Body body = wrld.createBody(defBod);

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

        return body;
    }
}
