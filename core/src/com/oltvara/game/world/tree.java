package com.oltvara.game.world;

import static com.oltvara.game.mainGame.*;
import static com.oltvara.game.mainGame.TILESIZE;
import static com.oltvara.game.handlers.physicsVars.PPM;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.oltvara.game.world.wrldHandlers.treeType;

import java.util.ArrayList;
import static com.oltvara.game.handlers.texture.texTypesNames.*;

public class tree {

    private ArrayList<leafLayer> leaves, backLeaves;
    private Color leafCol, trunkCol;
    private Vector2 relPOS, renPOS;
    private int size, shadowSize;
    private TextureRegion trunkTex;
    private boolean hasLeaves, hasBackLeaves;

    private Texture shadow;
    private Vector2 trOffset;

    public tree(treeType trType, Vector2 pos, int chunkOffset) {
        float sDColMod = (float)fct.random() + 1;

        trOffset = trType.getTreeOffset();
        float lfSDCol = trType.getLfSDCol() / sDColMod;
        float trSDCol = trType.getTrSDCol() / sDColMod;

        String txName = trType.pickTx();

        hasLeaves = true;

        if (trType.getTreeTypeName() == COLOURFULTREE) {
            hasBackLeaves = true;
            txName = txName.replace("back_", "front_");
        }

        //These trunk textures don't have branches drawn rn
        if (!txName.equals("medium_1-1-1") && !txName.equals("medium_1-1-2")) {
            //random chance to not have leaves
            if (fct.random() < 0.05) {
                hasLeaves = false;
                hasBackLeaves = false;
            }
        }

        trunkTex = trType.getTrunk(txName);

        leaves = new ArrayList<>();
        backLeaves = new ArrayList<>();

        size = trunkTex.getRegionWidth();

        shadow = trType.getShadow();
        shadowSize = shadow.getWidth();

        relPOS = new Vector2();
        relPOS.x = (pos.x + 0.5f + chunkOffset) * TILESIZE / PPM + trOffset.x;
        relPOS.y = (pos.y + 0.5f) * TILESIZE / PPM + trOffset.y;

        renPOS = new Vector2();
        renPOS.x = relPOS.x * PPM - size / 2f;
        renPOS.y = relPOS.y * PPM - size / 2f;

        trunkCol = fct.gaussianCol(trType.getTrunkCol(), trSDCol);

        Color baseCol = fct.gaussianCol(trType.getLeafCol(), lfSDCol / 2);

        //Don't bother creating leaves array if they're not gonna be drawn
        if (hasLeaves) {
            for (Array<TextureAtlas.AtlasRegion> layer : trType.getLeaves(txName)) {
                leafCol = fct.gaussianCol(baseCol, lfSDCol);
                int rand = fct.randomInt(12);
                leaves.add(new leafLayer(relPOS, layer, leafCol, rand));
            }
        }

        if (hasBackLeaves) {
            txName = txName.replace("front_", "back_");
            for (Array<TextureAtlas.AtlasRegion> layer : trType.getLeaves(txName)) {
                leafCol = fct.gaussianCol(baseCol, lfSDCol);
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