package com.oltvara.game.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.oltvara.game.gamestates.play;
import com.oltvara.game.handlers.inputControl;
import com.oltvara.game.mainGame;
import com.oltvara.game.handlers.contactListener;
import com.oltvara.game.handlers.physicsVars;

import static com.oltvara.game.mainGame.fct;
import static com.oltvara.game.handlers.physicsVars.PPM;

public class mainChar extends sprite {

    private contactListener cl;
    private final float MAXSPEED = 1.25f;
    private final float ACC = 3f;
    private final int BASE_JUMP = 50;
    private final short MAXJUMPVEL = 3;
    private final short FRICTION = 3;
    private Vector2 pos, vel, prevVel;
    private boolean jumping, jumpAnimIsPlaying, apexPlayed, landingPlayed;

    //Animation stuff
    private TextureRegion[] walkRight, walkLeft, idleRight, idleLeft, jumpRight, apexRight, fallingRight, landingRight, slowFallRight, jumpLeft, apexLeft, fallingLeft, landingLeft, slowFallLeft;
    private short cDirection, pDirection;
    private animSwitch cAnim, pAnim;
    private short animSpeed = 8;

    public mainChar(Body body) {
        super(body);
        vel = new Vector2(0,0);
        prevVel = new Vector2(0,0);
        offset = new Vector2(2, 0);
        body.setUserData(this);
        pos = body.getPosition();

        cl = play.getCL();

        //set anim textures
        //walking
        walkRight = mainGame.src.getTXfromAtlas("mainCharAtlas", "rightWalk").toArray();
        walkLeft = mainGame.src.getTXfromAtlas("mainCharAtlas", "leftWalk").toArray();

        //idle
        idleRight = mainGame.src.getTXfromAtlas("mainCharAtlas", "rightIdle").toArray();
        idleLeft = mainGame.src.getTXfromAtlas("mainCharAtlas", "leftIdle").toArray();

        //right Jump
        jumpRight = mainGame.src.getTXfromAtlas("mainCharAtlas", "rightJump").toArray();
        apexRight = mainGame.src.getTXfromAtlas("mainCharAtlas", "rightApex").toArray();
        slowFallRight = mainGame.src.getTXfromAtlas("mainCharAtlas", "rightSlowFall").toArray();
        fallingRight = mainGame.src.getTXfromAtlas("mainCharAtlas", "rightFalling").toArray();
        landingRight = mainGame.src.getTXfromAtlas("mainCharAtlas", "rightLanding").toArray();

        //left Jump
        jumpLeft = mainGame.src.getTXfromAtlas("mainCharAtlas", "leftJump").toArray();
        apexLeft = mainGame.src.getTXfromAtlas("mainCharAtlas", "leftApex").toArray();
        slowFallLeft = mainGame.src.getTXfromAtlas("mainCharAtlas", "leftSlowFall").toArray();
        fallingLeft = mainGame.src.getTXfromAtlas("mainCharAtlas", "leftFalling").toArray();
        landingLeft = mainGame.src.getTXfromAtlas("mainCharAtlas", "leftLanding").toArray();

        setAnim(fallingRight, 1f / animSpeed, 0);
        cAnim = animSwitch.falling;
        cDirection = 1;
        jumpAnimIsPlaying = true;
        apexPlayed = true;
    }

    public void charMovement(float delta) {
        prevVel.set(vel);
        pos = body.getPosition();
        vel = body.getLinearVelocity();

        //set jump state
        if (inputControl.isTap(inputControl.JUMPBUT) && cl.isCharContact()) {
            jumpAnimIsPlaying = true;
            jumping = true;
        }
        if (inputControl.isReleased(inputControl.JUMPBUT)) {
            jumping = false;
        }

        //Control jump height based on length button was pressed while still allowing jump on tap not on release
        if (inputControl.isPressed(inputControl.JUMPBUT) && jumping) {
            if (body.getLinearVelocity().y < MAXJUMPVEL) {
                int holdTime = inputControl.heldTime(inputControl.JUMPBUT);
                float jump = BASE_JUMP * (holdTime / 8f) * delta + Math.abs(vel.x * 20);
                jump = (int) fct.constrain(jump, BASE_JUMP, 200 + Math.abs(vel.x * 20));

                body.applyForceToCenter(0, jump, true);
            } else {
                jumping = false;
            }
        }

        //apply force for movement
        if (inputControl.isPressed(inputControl.RIGHT)) {
            cDirection = 1;
            if (vel.x < MAXSPEED) {
                if (cl.isCharContact()) {
                    if (vel.x < 0) body.applyLinearImpulse(ACC * 3 * delta, 0f, pos.x, pos.y, true);
                    else body.applyLinearImpulse(ACC * delta, 0f, pos.x, pos.y, true);
                } else {
                    body.applyLinearImpulse(ACC / 2 * delta, 0f, pos.x, pos.y, true);
                }
            }
        }

        if (inputControl.isPressed(inputControl.LEFT)) {
            cDirection = -1;
            if (vel.x > -MAXSPEED) {
                if (cl.isCharContact()) {
                    if (vel.x > 0) body.applyLinearImpulse(-ACC * 3 * delta, 0f, pos.x, pos.y, true);
                    else body.applyLinearImpulse(-ACC * delta, 0f, pos.x, pos.y, true);
                } else {
                    body.applyLinearImpulse(-ACC / 2 * delta, 0f, pos.x, pos.y, true);
                }
            }
        }

        //stop movement - better than friction
        if (!inputControl.isPressed(inputControl.LEFT) && !inputControl.isPressed(inputControl.RIGHT)) {
            body.applyLinearImpulse(-(vel.x * ACC * FRICTION * delta), 0f, pos.x, pos.y, true);
        }

        updateAnim();
    }

    private enum animSwitch {
        walk,
        idle,
        jumping,
        apex,
        slowFall,
        falling,
        landing
    }

    private void updateAnim() {

        //Jump anim conditions
        if (jumpAnimIsPlaying) {
            if (body.getLinearVelocity().y > 0 && cAnim != animSwitch.apex && cAnim != animSwitch.falling) {
                cAnim = animSwitch.jumping;
            }
            if (body.getLinearVelocity().y < 0 && !apexPlayed && !cl.isCharContact()) {
                if (cAnim != animSwitch.apex) setLoopsNum(0);
                cAnim = animSwitch.apex;
            }
            if (body.getLinearVelocity().y < 0 && apexPlayed && !cl.isCharContact()) {
                cAnim = animSwitch.slowFall;

                if (body.getLinearVelocity().y < -2.5f) {
                    cAnim = animSwitch.falling;
                }
            }
            if (cl.isCharContact() && !landingPlayed) {
                if (cAnim != animSwitch.landing) setLoopsNum(0);
                cAnim = animSwitch.landing;
            }
            if (landingPlayed) {
                jumpAnimIsPlaying = false;
                landingPlayed = false;
                apexPlayed = false;
                cAnim = animSwitch.idle;
            }

            if (cAnim == animSwitch.apex && getLoopsNum() >= 1) apexPlayed = true;
            if (cAnim == animSwitch.landing && getLoopsNum() >= 1) landingPlayed = true;
        }

        //Walking anim conditions
        if (!jumpAnimIsPlaying) {
            if (inputControl.isPressed(inputControl.RIGHT)) {
                cAnim = animSwitch.walk;
            }

            if (inputControl.isPressed(inputControl.LEFT)) {
                cAnim = animSwitch.walk;
            }

            if (!inputControl.isPressed(inputControl.LEFT) && !inputControl.isPressed(inputControl.RIGHT)) {
                cAnim = animSwitch.idle;
            }

            if (body.getLinearVelocity().y < -2f) {
                jumpAnimIsPlaying = true;
                apexPlayed = true;
            }
        }


        if (cDirection != pDirection) offset.x *= -1;

        //
        if (cAnim != pAnim || cDirection != pDirection) {
            pAnim = cAnim;
            pDirection = cDirection;

            switch (cAnim) {
                case idle:
                    if (cDirection == 1) setAnim(idleRight, 1f / animSpeed, 0);
                    if (cDirection == -1) setAnim(idleLeft, 1f / animSpeed, 0);
                    break;
                case walk:
                    if (cDirection == 1) setAnim(walkRight, 1f / animSpeed, 0);
                    if (cDirection == -1) setAnim(walkLeft, 1f / animSpeed, 0);
                    break;
                case jumping:
                    if (cDirection == 1) setAnim(jumpRight, 1f / animSpeed, 0);
                    if (cDirection == -1) setAnim(jumpLeft, 1f / animSpeed, 0);
                    break;
                case apex:
                    if (cDirection == 1) setAnim(apexRight, 1f / animSpeed, 0);
                    if (cDirection == -1) setAnim(apexLeft, 1f / animSpeed, 0);
                    break;
                case slowFall:
                    if (cDirection == 1) setAnim(slowFallRight, 1f / animSpeed, 0);
                    if (cDirection == -1) setAnim(slowFallLeft, 1f / animSpeed, 0);
                    break;
                case falling:
                    if (cDirection == 1) setAnim(fallingRight, 1f / animSpeed, 0);
                    if (cDirection == -1) setAnim(fallingLeft, 1f / animSpeed, 0);
                    break;
                case landing:
                    if (cDirection == 1) setAnim(landingRight, 1f / (animSpeed / 1.33f), 0);
                    if (cDirection == -1) setAnim(landingLeft, 1f / animSpeed, 0);
                    break;
            }
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

        box.setAsBox(6 / PPM,30.5f / PPM);
        defFix.shape = box;
        defFix.filter.categoryBits = physicsVars.bitCHAR;
        defFix.filter.maskBits = physicsVars.bitGROUND;
        //defFix.restitution = 0.8f;
        body.createFixture(defFix).setUserData("mainChar");

        //create foot sensor
        box.setAsBox(5 / PPM, 0.5f / PPM, new Vector2(0.25f / PPM, -30 / PPM), 0);
        defFix.shape = box;
        defFix.filter.categoryBits = physicsVars.bitCHAR;
        defFix.filter.maskBits = physicsVars.bitGROUND;
        defFix.isSensor = true;
        body.createFixture(defFix).setUserData("sensor");

        return body;
    }
}
