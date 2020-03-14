package com.oltvara.game.world;

import static com.oltvara.game.mainGame.fct;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.oltvara.game.gamestates.play;
import com.oltvara.game.world.wrldHandlers.chunk;

import java.util.HashMap;

public class map {

    private HashMap<Integer, chunk> chunks;
    private chunk ch;

    private int leftHeight, rightHeight;
    private final int MAXCHANGE;

    public map(int maxChange) {
        this.MAXCHANGE = maxChange;

        chunks = new HashMap<Integer, chunk>();
        leftHeight = 5;
        rightHeight = 5;
    }

    public void render(SpriteBatch sb) {
        for(chunk ch : chunks.values()){
            ch.render(sb);
        }
    }

    public void updateTiles(int offset) {
        chunks.get(offset).updateTiles();
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
            rightHeight = (int)fct.random(0, MAXCHANGE);
        }

        ch = new chunk(leftHeight, rightHeight, MAXCHANGE / 5, 0.001f, offset);
        chunks.put(offset, ch);
    }

    public void removeChunk(int offset) {
        if (chunks.get(offset) == null) { return; }

        for (tile tl : chunks.get(offset).getTileMap().values()) {
            play.addBodToDestroy(tl.getBod());
        }
        chunks.remove(offset);

    }

    public boolean hasChunk(int offset) {
        return chunks.get(offset) != null;
    }
}
