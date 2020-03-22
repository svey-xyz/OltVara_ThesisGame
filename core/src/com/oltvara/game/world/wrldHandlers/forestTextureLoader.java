package com.oltvara.game.world.wrldHandlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.HashMap;

import static com.oltvara.game.mainGame.TILESIZE;
import static com.oltvara.game.mainGame.fct;
import static com.oltvara.game.world.wrldHandlers.physicsVars.PPM;

public class forestTextureLoader {

    public final int TREE = 0;
    public final int BUSH = 1;

    public final int SMALLBUSHYTREE = 0;
    public final int MEDIUMBUSHYTREE = 1;
    public final int MEDIUMSPIKYTREE = 2;
    public final int SMALLSPIKYTREE = 3;

    public final int GROUND = 0;
    public final int LIVEGROUND = 1;
    public final int DOUBLELIVEGROUND = 2;
    public final int ROCKS = 3;
    public final int GRASS = 4;

    public final int MEDIUMBUSH = 0;

    private TextureAtlas txAtlas;
    private TextureAtlas groundAtlas;

    private HashMap<Integer, treeType> treeTypes = new HashMap<Integer, treeType>();
    private HashMap<Integer, bushType> bushTypes = new HashMap<Integer, bushType>();
    private HashMap<Integer, String[]> tileNames = new HashMap<Integer, String[]>();;

    private HashMap<String, ArrayList<Array<TextureAtlas.AtlasRegion>>> leafTextures;
    private HashMap<String, TextureRegion> trunks;

    private HashMap<String, ArrayList<Array<TextureAtlas.AtlasRegion>>> bushTextures;
    private HashMap<String, TextureRegion> tiles;

    public forestTextureLoader() {
        groundAtlas = new TextureAtlas(Gdx.files.internal("resources/tiles/groundTiles.atlas"));

        leafTextures = new HashMap<>();
        trunks = new HashMap<>();

        bushTextures = new HashMap<>();

        tiles = new HashMap<>();

        createTreeTypes();
        createBushTypes();
        loadTiles();

        for (treeType trType : treeTypes.values()) {
            for (String treeName : trType.getNames()) {
                loadTreeTextures(treeName);
            }
        }

        for (bushType bsType : bushTypes.values()) {
            for (String bsName : bsType.getNames()) {
                loadBushTextures(bsName);
            }
        }
    }

    private void loadBushTextures(String name) {
        txAtlas = new TextureAtlas(Gdx.files.internal("resources/environment/foliage/" + name + ".atlas"));
        int bsLayers = (txAtlas.getRegions().size) / 12;
        bushTextures.put(name, loadLayers(bsLayers));
    }

    private void loadTreeTextures(String name) {
        txAtlas = new TextureAtlas(Gdx.files.internal("resources/environment/trees/" + name + ".atlas"));
        int lfLayers = (txAtlas.getRegions().size - 1) / 12;
        leafTextures.put(name, loadLayers(lfLayers));
        trunks.put(name, txAtlas.findRegion("trunk"));
    }

    private ArrayList<Array<TextureAtlas.AtlasRegion>> loadLayers(int layers) {
        ArrayList<Array<TextureAtlas.AtlasRegion>> trLayers = new ArrayList<>();
        Array<TextureAtlas.AtlasRegion> region;

        for (int i = 0; i < layers; i++) {
            region = txAtlas.findRegions("layer" + i);
            trLayers.add(region);
        }

        return trLayers;
    }

    public String pickTx(int plantType, int texType) {
        if (plantType == TREE) {
            int rnd = fct.randomInt((treeTypes.get(texType)).getNames().length);
            return (treeTypes.get(texType)).getNames()[rnd];
        }

        if (plantType == BUSH) {
            int rnd = fct.randomInt((bushTypes.get(texType)).getNames().length);
            return (bushTypes.get(texType)).getNames()[rnd];
        }

        return null;
    }

    public ArrayList<Array<TextureAtlas.AtlasRegion>> getLeaves(int plantType, String name) {
        if (plantType == TREE) return leafTextures.get(name);
        if (plantType == BUSH) return bushTextures.get(name);

        return null;
    }

    public TextureRegion getTrunk(String treeName) {
        return trunks.get(treeName);
    }

    public Color getLeafCols(int plantType, int texType) {
        if (plantType == TREE) return treeTypes.get(texType).getLeafColor();
        if (plantType == BUSH) return bushTypes.get(texType).getLeafColor();

        return null;
    }

    public Color getTrunkCol(int treeType) {
        return treeTypes.get(treeType).getTrunkColor();
    }

    public float getLfSDCol(int plantType, int texType) {
        if (plantType == TREE) return treeTypes.get(texType).getLfSDCol();
        if (plantType == BUSH) return bushTypes.get(texType).getLfSDCol();

        return 0;
    }

    public float getTrSDCol(int treeType) {
        return treeTypes.get(treeType).getTrSDCol();
    }

    public Vector2 getPosOffset(int plantType, int texType) {
        if (plantType == TREE) return treeTypes.get(texType).getTreeOffset();
        if (plantType == BUSH) return bushTypes.get(texType).getTreeOffset();

        return null;
    }

    public String[] getTileList(Integer ls) {
        return tileNames.get(ls);
    }

    public TextureRegion getTileTex(String name) {
        return tiles.get(name);
    }

    public void dispose() {
        txAtlas.dispose();
        groundAtlas.dispose();
    }

    private void createTreeTypes() {
        treeTypes.put(SMALLBUSHYTREE, new treeType(new Vector2((TILESIZE / 2f / PPM), (128 / 2f / PPM)),
                fct.fromRGB(240, 80, 80), fct.fromRGB(235, 205, 175), 0.08f, 0.01f,
                new String[]{"small_1-1-1", "small_1-1-2"}));

        treeTypes.put(MEDIUMBUSHYTREE, new treeType(new Vector2((TILESIZE / 2f / PPM), (256 / 2f / PPM)),
                fct.fromRGB(240, 80, 80), fct.fromRGB(235, 205, 175), 0.08f, 0.01f,
                new String[]{"medium_1-1-1", "medium_1-1-2", "medium_1-1-3"}));

        treeTypes.put(MEDIUMSPIKYTREE, new treeType(new Vector2((TILESIZE / 2f / PPM), (256 / 2f / PPM)),
                fct.fromRGB(255, 180, 80), fct.fromRGB(245, 116, 67), 0.05f, 0.004f,
                new String[]{"medium_1-2-1"}));

        treeTypes.put(SMALLSPIKYTREE, new treeType(new Vector2((TILESIZE / 2f / PPM), (128 / 2f / PPM)),
                fct.fromRGB(255, 180, 80), fct.fromRGB(245, 116, 67), 0.08f, 0.002f,
                new String[]{"small_1-2-1"}));
    }

    private void createBushTypes() {
        bushTypes.put(MEDIUMBUSH, new bushType(new Vector2(0, (24 / PPM)),
                fct.fromRGB(240, 240, 50), 0.08f,
                new String[]{"medium_1-1-1", "medium_1-1-2", "medium_1-1-3", "medium_1-1-4"}));
    }

    private void loadTiles() {
        tileNames.put(GRASS, new String[]{"grass-1-1", "grass-1-2", "grass-1-3"});
        tileNames.put(GROUND, new String[]{"ground-1-1", "ground-1-2", "groundSpeckle-1-1", "groundSpeckle-1-2"});
        tileNames.put(ROCKS, new String[]{"groundRock-1-1", "groundRockSpeckle-1-1"});
        tileNames.put(LIVEGROUND, new String[]{"groundLive-1-1", "groundLiveSpeckle-1-1"});
        tileNames.put(DOUBLELIVEGROUND, new String[]{"groundDLive-1-1", "groundDLiveSpeckle-1-1"});

        for (String[] listNames : tileNames.values()) {
            for (String name : listNames) {
                tiles.put(name, groundAtlas.findRegion(name));
            }
        }
    }
}
