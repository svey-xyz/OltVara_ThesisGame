package com.oltvara.game.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.oltvara.game.world.wrldHandlers.chunk;

import java.util.HashMap;

public class map {

    private HashMap<Integer, chunk> chunks;
    private chunk ch;

    public map() {
        chunks = new HashMap<Integer, chunk>();
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
        ch = new chunk(0, 20, 20 / 4, 0.7f, offset);
        chunks.put(offset, ch);
    }

    public void removeChunk(int offset) {
        chunks.remove(offset);
    }
}
