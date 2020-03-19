package com.oltvara.game.world;

import static com.oltvara.game.mainGame.*;
import static com.oltvara.game.mainGame.TILESIZE;
import static com.oltvara.game.world.wrldHandlers.physicsVars.PPM;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.oltvara.game.world.wrldHandlers.physicsVars;

import java.util.ArrayList;


public class tree {

    private ArrayList<leafLayer> leaves;
    private Color leafCol, trunkCol;
    private Vector2 relPOS;
    private float size;
    private TextureRegion trunkTex;

    public tree(Vector2 pos, int chunkOffset, int treeType) {
        float sDColMod = (float)fct.random() + 1;

        Vector2 trOffset = frTex.getPosOffset(frTex.TREE, treeType);
        float lfSDCol = frTex.getLfSDCol(frTex.TREE, treeType) / sDColMod;
        float trSDCol = frTex.getTrSDCol(treeType) / sDColMod;
        String txName = frTex.pickTx(frTex.TREE, treeType);

        trunkTex = frTex.getTrunk(txName);

        leaves = new ArrayList<>();

        size = trunkTex.getRegionWidth();

        relPOS = new Vector2();
        relPOS.x = (pos.x + 0.5f + chunkOffset) * TILESIZE / PPM + trOffset.x;
        relPOS.y = (pos.y + 0.5f) * TILESIZE / PPM + trOffset.y;

        trunkCol = fct.gaussianCol(frTex.getTrunkCol(treeType), trSDCol);

        for (Array<TextureAtlas.AtlasRegion> layer : frTex.getLeaves(frTex.TREE, txName)) {
            leafCol = fct.gaussianCol(frTex.getLeafCols(frTex.TREE, treeType), lfSDCol);
            int rand = fct.randomInt(12);
            leaves.add(new leafLayer(relPOS, layer, leafCol, rand));
        }
    }

    public void render(SpriteBatch sb) {
        sb.begin();
        sb.setColor(trunkCol);
        sb.draw(trunkTex,
                (relPOS.x * physicsVars.PPM - size / 2),
                (relPOS.y * physicsVars.PPM - size / 2)
        );


        sb.end();

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