package com.oltvara.game.world.wrldHandlers;

import static com.oltvara.game.mainGame.src;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.HashMap;

import static com.oltvara.game.mainGame.fct;

public class treeTextureLoader {

    public final int BUSHYTREE = 0;

    private TextureAtlas treeAtlas;

    private HashMap<String, ArrayList<Array<TextureAtlas.AtlasRegion>>> trees;
    private HashMap<String, TextureRegion> trunk;

    private String[] treeNames = {"small_1-1-1"};

    private Color[] leafColors = {fct.fromRGB(240, 80, 80)};

    public treeTextureLoader() {
        trees = new HashMap<>();

        for (int i = 0; i < treeNames.length; i++) {
            loadTextures(treeNames[i]);
        }
    }

    private void loadTextures(String name) {
        treeAtlas = new TextureAtlas(Gdx.files.internal("resources/environment/trees/" + name + ".atlas"));
        trees.put(name, loadLeaves(name, 5));
    }

    private TextureRegion loadTrunk(String treeName) {
        return treeAtlas.findRegion("trunk");
    }

    private ArrayList<Array<TextureAtlas.AtlasRegion>> loadLeaves(String treeName, int leafLayers) {
        ArrayList<Array<TextureAtlas.AtlasRegion>> trLayers = new ArrayList<>();
        Array<TextureAtlas.AtlasRegion> region;

        for (int i = 0; i < leafLayers; i++) {
            region = treeAtlas.findRegions("layer" + i);
            trLayers.add(region);
        }

        return trLayers;
    }

    public ArrayList<Array<TextureAtlas.AtlasRegion>> getTree(String name) {
        return trees.get(name);
    }

    public Color getLeafCols(int treeType) {
        return leafColors[treeType];
    }
}
