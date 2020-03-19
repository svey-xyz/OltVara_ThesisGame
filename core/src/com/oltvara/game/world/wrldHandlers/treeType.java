package com.oltvara.game.world.wrldHandlers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import static com.oltvara.game.mainGame.fct;

class treeType {

    private String[] names;

    private Color leafColor, trunkColor;
    private float lfSDCol, trSDCol;
    private Vector2 offset;

    treeType(Vector2 offset, Color lfCol, Color trCol, float lfSDCol, float trSDCol, String[] names) {

        this.names = names;
        this.leafColor = lfCol;
        this.trunkColor = trCol;
        this.lfSDCol = lfSDCol;
        this.trSDCol = trSDCol;
        this.offset = offset;
    }

    String[] getNames() { return names; }
    Color getLeafColor() { return leafColor; }
    Color getTrunkColor() { return trunkColor; }
    Vector2 getTreeOffset() { return offset; }
    float getLfSDCol() { return lfSDCol; }
    float getTrSDCol() { return trSDCol; }

}
