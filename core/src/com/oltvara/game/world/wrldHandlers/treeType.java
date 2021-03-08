package com.oltvara.game.world.wrldHandlers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.oltvara.game.handlers.texture.texTypesNames;

import java.util.ArrayList;
import java.util.HashMap;

import static com.oltvara.game.mainGame.fct;

public class treeType {

    private texTypesNames treeTypeName;
    private String[] names;

    private Color leafColor, trunkColor;
    private float lfSDCol, trSDCol;
    private Vector2 offset;

    private HashMap<String, TextureRegion> trunks;
    private HashMap<String, ArrayList<Array<TextureAtlas.AtlasRegion>>> leafTextures;

    private Texture shadow;

    public treeType(texTypesNames treeType, Vector2 offset, Color lfCol, Color trCol, float lfSDCol, float trSDCol, String[] names, Texture shadow) {
        this.treeTypeName = treeType;
        this.names = names;
        this.leafColor = lfCol;
        this.trunkColor = trCol;
        this.lfSDCol = lfSDCol;
        this.trSDCol = trSDCol;
        this.offset = offset;
        this.shadow = shadow;

        trunks = new HashMap<>();
        leafTextures = new HashMap<>();
    }

    public String pickTx() {
        int rnd = fct.randomInt(names.length);
        return (names[rnd]);
    }

    public String[] getNames() { return names; }

    public Color getLeafCol() { return leafColor; }
    public Color getTrunkCol() { return trunkColor; }
    public Vector2 getTreeOffset() { return offset; }
    public float getLfSDCol() { return lfSDCol; }
    public float getTrSDCol() { return trSDCol; }
    public texTypesNames getTreeTypeName() { return treeTypeName; }

    public TextureRegion getTrunk(String name) { return trunks.get(name); }
    public ArrayList<Array<TextureAtlas.AtlasRegion>> getLeaves(String name) { return leafTextures.get(name); }
    public Texture getShadow() { return shadow; }

    public void addTrunk(String treeName, TextureRegion trunk) { trunks.put(treeName, trunk); }
    public void addLeaves(String treeName, ArrayList<Array<TextureAtlas.AtlasRegion>> leaves) { leafTextures.put(treeName, leaves); }

}
