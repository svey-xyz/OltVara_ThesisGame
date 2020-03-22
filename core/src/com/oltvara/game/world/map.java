package com.oltvara.game.world;

import static com.oltvara.game.mainGame.fct;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.oltvara.game.gamestates.play;
import com.oltvara.game.world.wrldHandlers.chunk;
import java.util.HashMap;


public class map {

    private HashMap<Integer, chunk> chunks;

    private final int AVGCHANGE;
    private final int MINHEIGHT = 3;

    public map(int avgChange) {
        this.AVGCHANGE = avgChange;

        //chunks stored in hashmap with their offset as key
        chunks = new HashMap<Integer, chunk>();
    }

    public void addChunk(int offset) {
        int leftHeight, rightHeight , rightChange = 0;
        float maxChange = 0;

        //max height varies per chunk based on gaussian distribution to create more dynamic landscape
        int chAmt = (int)fct.randomGaussian(AVGCHANGE / 4f, AVGCHANGE);

        //Check if there is a chunk next to the one you're creating
        //if so the heights were they meet need to be even, if not random height
        if (hasChunk(offset - 1)) {
            leftHeight = chunks.get(offset - 1).getHeightMap()[chunks.get(offset - 1).getHeightMap().length - 1];
            maxChange = leftHeight - MINHEIGHT;
        } else {
            leftHeight = (int)fct.random(MINHEIGHT, chAmt);
        }
        if (hasChunk(offset + 1)) {
            rightHeight = chunks.get(offset + 1).getHeightMap()[0];
            maxChange = Math.max(maxChange, rightHeight - MINHEIGHT);
        } else {
            rightChange = (int)fct.random(MINHEIGHT, chAmt);
            rightHeight = (int)fct.random(MINHEIGHT, chAmt + leftHeight / 2f);
        }
        if (!hasChunk(offset - 1)) {
            leftHeight = (int)fct.random(MINHEIGHT, chAmt + rightChange / 2f);
        }

        /*adjust roughness and height displacement based on the amount of change compared to the max amount of change possible.
        constraining maxChange and change to AVGCHANGE creates rough hills more often,
        otherwise they only really appear when the max possible change ahs been met, which is rarely*/
        maxChange = (float)fct.constrain(Math.max(maxChange, chAmt * 1.5f), 0, AVGCHANGE);

        //dividing by maxChange puts change 0-1 which can then be mapped to desired roughness
        float change = (float)fct.constrain(Math.abs(leftHeight - rightHeight), 0, AVGCHANGE) / maxChange;;

        /*putting change to a power creates an exponential relationship with the output for roughness rather than a linear one
        this is desirable so that roughness only really occurs ar higher change values*/
        float rough = (float)fct.map(Math.pow(change, 8), 0, 1, 0, 0.6);

        /*displacement can be a linear relationship, increasing displacement allows for spiky mountains in high change areas
        that become impassable.*/
        float dis = (float)fct.map(change, 0, 1, 5,  1.5);

        /*displacement is also based on the avg change amount for the chunk- don't want high displacement on a small change chunk
        it would cause weird roughness even with low rough values as the midpoint displacement would be trying to displace each
        step more than necessary*/
        chunks.put(offset, new chunk(leftHeight, rightHeight, (int)(chAmt / dis), rough, offset));
    }

    public void removeChunk(int offset) {
        if (chunks.get(offset) == null) { return; }

        //queue physics bodies from tiles to be destroyed after world step
        for (Body b : chunks.get(offset).getBodies()) {
            play.addBodToDestroy(b);
        }
        chunks.remove(offset);
    }

    public void renderBack(SpriteBatch sb) {
        for(chunk ch : chunks.values()){
            ch.renderBack(sb);
        }
    }

    public void renderMain(SpriteBatch sb) {
        for(chunk ch : chunks.values()){
            ch.renderTiles(sb);
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

    public boolean hasChunk(int offset) {
        return chunks.get(offset) != null;
    }
}
