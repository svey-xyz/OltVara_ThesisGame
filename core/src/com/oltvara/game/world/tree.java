package com.oltvara.game.world;

import static com.oltvara.game.mainGame.*;
import static com.oltvara.game.mainGame.TILESIZE;
import static com.oltvara.game.world.wrldHandlers.physicsVars.PPM;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;


public class tree {

    private ArrayList<leafLayer> leaves, backLeaves;
    private Color leafCol, trunkCol;
    private Vector2 relPOS, renPOS;
    private int size, shadowSize;
    private TextureRegion trunkTex;
    private boolean hasLeaves, hasBackLeaves;

    Texture shadow;
    Vector2 trOffset;

    public tree(Vector2 pos, int chunkOffset, int treeType) {

        float sDColMod = (float)fct.random() + 1;

        trOffset = frTex.getPosOffset(frTex.TREE, treeType);
        float lfSDCol = frTex.getLfSDCol(frTex.TREE, treeType) / sDColMod;
        float trSDCol = frTex.getTrSDCol(treeType) / sDColMod;
        String txName = frTex.pickTx(frTex.TREE, treeType);

        hasLeaves = true;

        if (treeType == frTex.COLOURFULTREE) {
            hasBackLeaves = true;
            txName = txName.replace("back_", "front_");
        }

        //These trunk textures don't have branches drawn rn
        if (!txName.equals("medium_1-1-1") && !txName.equals("medium_1-1-2") && !txName.equals("medium_1-1-4")) {
            if (fct.random() < 0.05) {
                hasLeaves = false;
                hasBackLeaves = false;
            }
        }

        trunkTex = frTex.getTrunk(txName);

        leaves = new ArrayList<>();
        backLeaves = new ArrayList<>();

        size = trunkTex.getRegionWidth();

        if (size == 128) {
            shadow = frTex.getTreeShadow("smallShadow");
        } else {
            shadow = frTex.getTreeShadow("mediumShadow");
        }

        shadowSize = shadow.getWidth();


        relPOS = new Vector2();
        relPOS.x = (pos.x + 0.5f + chunkOffset) * TILESIZE / PPM + trOffset.x;
        relPOS.y = (pos.y + 0.5f) * TILESIZE / PPM + trOffset.y;

        renPOS = new Vector2();
        renPOS.x = relPOS.x * PPM - size / 2f;
        renPOS.y = relPOS.y * PPM - size / 2f;

        trunkCol = fct.gaussianCol(frTex.getTrunkCol(treeType), trSDCol);

        //Don't bother creating leaves array if they're not gonna be drawn
        if (hasLeaves) {
            for (Array<TextureAtlas.AtlasRegion> layer : frTex.getLeaves(frTex.TREE, txName)) {
                leafCol = fct.gaussianCol(frTex.getLeafCols(frTex.TREE, treeType), lfSDCol);
                int rand = fct.randomInt(12);
                leaves.add(new leafLayer(relPOS, layer, leafCol, rand));
            }
        }

        if (hasBackLeaves) {
            txName = txName.replace("front_", "back_");
            for (Array<TextureAtlas.AtlasRegion> layer : frTex.getLeaves(frTex.TREE, txName)) {
                leafCol = fct.gaussianCol(frTex.getLeafCols(frTex.TREE, treeType), lfSDCol);
                int rand = fct.randomInt(12);
                backLeaves.add(new leafLayer(relPOS, layer, leafCol, rand));
            }
        }
    }

    public void render(SpriteBatch sb) {
        for (int i = leaves.size() - 1; i >= 0; i--) {
            leaves.get(i).render(sb, leaves.get(i).getCol());
        }

        sb.setColor(trunkCol);
        sb.draw(trunkTex, renPOS.x, renPOS.y);

        sb.draw(shadow,
                (relPOS.x * PPM - shadowSize / 2f),
                ((relPOS.y - trOffset.y) * PPM - shadowSize + TILESIZE / 2f)
        );

        for (int i = backLeaves.size() - 1; i >= 0; i--) {
            backLeaves.get(i).render(sb, backLeaves.get(i).getCol());
        }
    }

    public void update(float dt) {
        for (int i = leaves.size() - 1; i >= 0; i--) {
            leaves.get(i).update(dt);
        }
        for (int i = backLeaves.size() - 1; i >= 0; i--) {
            backLeaves.get(i).update(dt);
        }
    }

    public Vector2 getRenPOS() {
        return renPOS;
    }

    public int getSize() {
        return size;
    }
}