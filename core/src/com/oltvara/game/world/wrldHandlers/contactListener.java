package com.oltvara.game.world.wrldHandlers;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class contactListener implements ContactListener {

    private int charContact;

    public void beginContact(Contact c) {

        Fixture cfa = c.getFixtureA();
        Fixture cfb = c.getFixtureB();

        if(cfa.getUserData() != null && cfa.getUserData().equals("sensor")) {
            charContact++;
        }

        if(cfb.getUserData() != null && cfb.getUserData().equals("sensor")) {
            charContact++;
        }
    }

    public void endContact(Contact c) {

        Fixture cfa = c.getFixtureA();
        Fixture cfb = c.getFixtureB();

        if(cfa.getUserData() != null && cfa.getUserData().equals("sensor")) {
            charContact--;
        }

        if(cfb.getUserData() != null && cfb.getUserData().equals("sensor")) {
            charContact--;
        }
    }

    public boolean isCharContact() {
        return charContact > 0;
    }

    public Array<Body> getBodiesToRemove() {
        return new Array<Body>();
    }

    public void preSolve(Contact contact, Manifold oldManifold) {}
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}
