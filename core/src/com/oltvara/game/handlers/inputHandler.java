package com.oltvara.game.handlers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class inputHandler extends InputAdapter {

    public boolean keyDown(int k) {
        if (k == Input.Keys.SPACE || k == Input.Keys.W || k == Input.Keys.UP) {
            inputControl.setKey(inputControl.JUMPBUT, true);
        }
        if (k == Input.Keys.D || k == Input.Keys.RIGHT) {
            inputControl.setKey(inputControl.RIGHT, true);
        }
        if (k == Input.Keys.A || k == Input.Keys.LEFT) {
            inputControl.setKey(inputControl.LEFT, true);
        }
        return true;
    }

    public boolean keyUp(int k) {
        if (k == Input.Keys.SPACE || k == Input.Keys.W || k == Input.Keys.UP) {
            inputControl.setKey(inputControl.JUMPBUT, false);
        }
        if (k == Input.Keys.D || k == Input.Keys.RIGHT) {
            inputControl.setKey(inputControl.RIGHT, false);
        }
        if (k == Input.Keys.A || k == Input.Keys.LEFT) {
            inputControl.setKey(inputControl.LEFT, false);
        }
        return true;
    }

}
