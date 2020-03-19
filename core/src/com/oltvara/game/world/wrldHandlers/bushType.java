package com.oltvara.game.world.wrldHandlers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class bushType {

    private String[] names;

    private Color leafColor;
    private float lfSDCol;
    private Vector2 offset;

    bushType(Vector2 offset, Color lfCol, float lfSDCol, String[] names) {

        this.names = names;
        this.leafColor = lfCol;
        this.lfSDCol = lfSDCol;
        this.offset = offset;
    }

    String[] getNames() { return names; }
    Color getLeafColor() { return leafColor; }
    Vector2 getTreeOffset() { return offset; }
    float getLfSDCol() { return lfSDCol; }

}

