package com.oltvara.game.world;
import static com.oltvara.game.mainGame.*;
import static com.oltvara.game.mainGame.TILESIZE;
import static com.oltvara.game.handlers.physicsVars.PPM;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.oltvara.game.world.wrldHandlers.bushType;

import java.util.ArrayList;

public class bush {

    private ArrayList<leafLayer> leaves;
    private Vector2 relPOS, renPOS;
    private int size;

    public bush(bushType bsType, Vector2 pos, int chunkOffset) {
        float sDColMod = (float)fct.random() + 1;

        Vector2 bsOffset = bsType.getBushOffset();
        float lfSDCol = bsType.getLfSDCol() / sDColMod;
        String txName = bsType.pickTx();

        leaves = new ArrayList<>();

        relPOS = new Vector2();
        relPOS.x = (pos.x + 0.5f + chunkOffset) * TILESIZE / PPM + bsOffset.x;
        relPOS.y = (pos.y + 0.5f) * TILESIZE / PPM + bsOffset.y;

        size = bsType.getLeaves(txName).get(0).get(0).originalWidth;

        renPOS = new Vector2();
        renPOS.x = relPOS.x * PPM - size / 2f;
        renPOS.y = relPOS.y * PPM - size / 2f;

        for (Array<TextureAtlas.AtlasRegion> layer : bsType.getLeaves(txName)) {
            int rand = fct.randomInt(12);
            leaves.add(new leafLayer(relPOS, layer, fct.gaussianCol(bsType.getLeafCol(), lfSDCol), rand));
        }
    }

    public void render(SpriteBatch sb) {
        for (int i = leaves.size() - 1; i >= 0; i--) {
            leaves.get(i).render(sb, leaves.get(i).getCol());
        }
    }

    public void update(float dt) {
        for (int i = leaves.size() - 1; i >= 0; i--) {
            leaves.get(i).update(dt);
        }
    }

    public Vector2 getRenPOS() {
        return renPOS;
    }

    public int getSize() {
        return size;
    }
}
