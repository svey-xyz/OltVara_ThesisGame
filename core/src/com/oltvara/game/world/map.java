package com.oltvara.game.world;

import static com.oltvara.game.mainGame.fct;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.oltvara.game.gamestates.play;
import com.oltvara.game.world.wrldHandlers.chunk;
import java.util.HashMap;


public class map {

    private HashMap<Integer, chunk> chunks;
    private chunk ch;

    private int leftHeight, rightHeight, leftChange, rightChange;
    private final int MAXCHANGE;

    public map(int maxChange) {
        this.MAXCHANGE = maxChange;

        chunks = new HashMap<Integer, chunk>();
    }

    public void render(SpriteBatch sb) {
        for(chunk ch : chunks.values()){
            ch.render(sb);
        }
    }

    public void renderFront(SpriteBatch sb) {
        for(chunk ch : chunks.values()){
            ch.renderFront(sb);
        }
    }

    public void update(float delta) {
        for(chunk ch : chunks.values()){
            ch.update(delta);
        }
    }

    public void addChunk(int offset) {
        if (hasChunk(offset - 1)) {
            leftHeight = chunks.get(offset - 1).getHeightMap()[chunks.get(offset - 1).getHeightMap().length - 1];
        } else {
            leftHeight = (int)fct.random(0, MAXCHANGE);
        }
        if (hasChunk(offset + 1)) {
            rightHeight = chunks.get(offset + 1).getHeightMap()[0];
        } else {
            rightChange = (int)fct.random(0, MAXCHANGE);
            rightHeight = (int)fct.random(0, MAXCHANGE + leftHeight / 2f);
        }
        if (!hasChunk(offset - 1)) {
            leftHeight = (int)fct.random(0, MAXCHANGE + rightChange / 2f);
        }

        ch = new chunk(leftHeight, rightHeight, MAXCHANGE / 5, 0.00001f, offset);
        chunks.put(offset, ch);
    }

    public void removeChunk(int offset) {
        if (chunks.get(offset) == null) { return; }

        for (Body b : chunks.get(offset).getBodies()) {
            play.addBodToDestroy(b);
        }
        chunks.remove(offset);
    }

    public boolean hasChunk(int offset) {
        return chunks.get(offset) != null;
    }
}
