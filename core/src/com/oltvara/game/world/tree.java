package com.oltvara.game.world;

import static com.oltvara.game.mainGame.trTex;
import static com.oltvara.game.mainGame.fct;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;


public class tree {

    private ArrayList<leafLayer> leaves;
    private Color col;

    public tree(Vector2 pos, String name, int treeType) {

        leaves = new ArrayList<>();

        for (Array<TextureAtlas.AtlasRegion> layer : trTex.getTree(name)) {
            col = fct.gaussianCol(trTex.getLeafCols(treeType), 0.07f);
            leaves.add(new leafLayer(pos, layer, col));
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