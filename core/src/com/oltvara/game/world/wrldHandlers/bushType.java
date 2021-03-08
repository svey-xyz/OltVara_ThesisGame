package com.oltvara.game.world.wrldHandlers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.HashMap;

import static com.oltvara.game.mainGame.fct;

public class bushType {

    private String[] names;

    private Color leafColor;
    private float lfSDCol;
    private Vector2 offset;

    private HashMap<String, ArrayList<Array<TextureAtlas.AtlasRegion>>> bushTextures;

    public bushType(Vector2 offset, Color lfCol, float lfSDCol, String[] names) {
        this.names = names;
        this.leafColor = lfCol;
        this.lfSDCol = lfSDCol;
        this.offset = offset;

        bushTextures = new HashMap<>();
    }

    public String pickTx() {
        int rnd = fct.randomInt(names.length);
        return (names[rnd]);
    }

    public String[] getNames() { return names; }
    public Color getLeafCol() { return leafColor; }
    public Vector2 getBushOffset() { return offset; }
    public float getLfSDCol() { return lfSDCol; }

    public ArrayList<Array<TextureAtlas.AtlasRegion>> getLeaves(String name) { return bushTextures.get(name); }

    public void addLeaves(String treeName, ArrayList<Array<TextureAtlas.AtlasRegion>> leaves) { bushTextures.put(treeName, leaves); }
}

