package com.oltvara.game.handlers;

import com.oltvara.game.gamestates.gameState;
import com.oltvara.game.gamestates.play;
import com.oltvara.game.mainGame;

import java.util.Stack;

public class stateHandler {

    private mainGame game;

    private Stack<gameState> gameStates;

    public static final int PLAY = 2;


    public stateHandler(mainGame game) {
        this.game = game;
        gameStates = new Stack<gameState>();
        pushState(PLAY);
    }

    public mainGame game() { return game; }

    public void update(float delta) {
        gameStates.peek().update(delta);

    }

    public void render() {
        gameStates.peek().render();
    }

    private gameState getState(int state) {
        if (state == PLAY) { return new play(this); }
        return null;
    }

    public void setState(int state) {
        popState();
        pushState(state);
    }

    public void popState() {
        gameState state = gameStates.pop();
        state.dispose();
    }

    public void pushState(int state) {
        gameStates.push(getState(state));
    }

}
