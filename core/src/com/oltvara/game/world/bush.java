package com.oltvara.game.world;
import static com.oltvara.game.mainGame.*;
import static com.oltvara.game.mainGame.TILESIZE;
import static com.oltvara.game.world.wrldHandlers.physicsVars.PPM;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

public class bush {

    private ArrayList<leafLayer> leaves;

    public bush(Vector2 pos, int chunkOffset, int bushType) {
        float sDColMod = (float)fct.random() + 1;

        Vector2 bsOffset = frTex.getPosOffset(frTex.BUSH, bushType);
        float lfSDCol = frTex.getLfSDCol(frTex.BUSH, bushType) / sDColMod;
        String txName = frTex.pickTx(frTex.BUSH, bushType);

        leaves = new ArrayList<>();

        Vector2 relPOS = new Vector2();
        relPOS.x = (pos.x + 0.5f + chunkOffset) * TILESIZE / PPM + bsOffset.x;
        relPOS.y = (pos.y + 0.5f) * TILESIZE / PPM + bsOffset.y;


        for (Array<TextureAtlas.AtlasRegion> layer : frTex.getLeaves(frTex.BUSH, txName)) {
            Color leafCol = fct.gaussianCol(frTex.getLeafCols(frTex.BUSH, bushType), lfSDCol);
            int rand = fct.randomInt(12);
            leaves.add(new leafLayer(relPOS, layer, leafCol, rand));
        }
    }

    public void render(SpriteBatch sb) {
        for (leafLayer leaf : leaves) {
            leaf.render(sb, leaf.getCol());
        }
    }

    public void update(float dt) {
        for (leafLayer leaf : leaves) {
            leaf.update(dt);
        }
    }
}
