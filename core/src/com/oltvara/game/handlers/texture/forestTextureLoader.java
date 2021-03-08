package com.oltvara.game.handlers.texture;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.oltvara.game.world.wrldHandlers.bushType;
import com.oltvara.game.world.wrldHandlers.treeType;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import static com.oltvara.game.handlers.physicsVars.PPM;
import static com.oltvara.game.handlers.texture.texTypesNames.*;
import static com.oltvara.game.mainGame.*;

public class forestTextureLoader {

    private final String BIOME;

    //should manage these the same way as SRC Handler
    //Really this whole class needs to be adapted to be more like srcHandler and to either extend or replace it
    private TextureAtlas plantAtlas;
    private TextureAtlas tileAtlas;
    private TextureAtlas rockAtlas;
    private TextureAtlas wellAtlas;

    private HashMap<texTypesNames, treeType> treeTypes = new HashMap<texTypesNames, treeType>();
    private HashMap<texTypesNames, bushType> bushTypes = new HashMap<texTypesNames, bushType>();
    private HashMap<texTypesNames, String[]> tileNames = new HashMap<texTypesNames, String[]>();
    private HashMap<texTypesNames, String[]> rockNames = new HashMap<texTypesNames, String[]>();

    private HashMap<String, TextureRegion> tiles;
    private HashMap<String, TextureRegion> rocks;

    public forestTextureLoader(String biome) {
        this.BIOME = biome;

        tileAtlas = new TextureAtlas(Gdx.files.internal("resources/tiles/groundTiles.atlas"));
        rockAtlas = new TextureAtlas(Gdx.files.internal("resources/environment/" + BIOME + "/rocks.atlas"));
        wellAtlas = new TextureAtlas(Gdx.files.internal("resources/environment/" + BIOME + "/well.atlas"));

        tiles = new HashMap<>();
        rocks = new HashMap<>();

        createTreeTypes();
        createBushTypes();
        loadTiles();
        loadRocks();

        //easy loading of textures rather than doing each one manually
        //means each texture needs its own atlas though
        for (treeType trType : treeTypes.values()) {
            for (String treeName : trType.getNames()) {
                loadTreeTextures(trType, treeName);
            }
        }
        for (bushType bsType : bushTypes.values()) {
            for (String bsName : bsType.getNames()) {
                loadBushTextures(bsType, bsName);
            }
        }
    }

    /*bush texture loading is different than tree so that multiple bushes can be stored in one texture pack to decrease
    memory usage.*/
    private void loadBushTextures(bushType bsType, String name) {
        plantAtlas = new TextureAtlas(Gdx.files.internal("resources/environment/" + BIOME + "/foliage/" + name.substring(0, name.length() - 6) + "Bushes.atlas"));
        int bsLayers = 0, i = 0;
        boolean hasLayers = true;

        //Determines how many layers that bush has in the atlas
        while (hasLayers) {
            if (plantAtlas.findRegions(name + "_layer" + i).size > 0) {
                bsLayers += (plantAtlas.findRegions(name + "_layer" + i).size) / 12;
                i++;
            } else {
                hasLayers = false;
            }
        }
        //load those layers
        bsType.addLeaves(name, loadLayers(name + "_", bsLayers));
    }

    private void loadTreeTextures(treeType tr, String name) {
        plantAtlas = new TextureAtlas(Gdx.files.internal("resources/environment/" + BIOME + "/trees/" + name + ".atlas"));
        int lfLayers = (plantAtlas.getRegions().size - 1) / 12;
        tr.addLeaves(name, loadLayers("", lfLayers));
        tr.addTrunk(name, plantAtlas.findRegion("trunk"));
    }

    private ArrayList<Array<TextureAtlas.AtlasRegion>> loadLayers(String name, int layers) {
        ArrayList<Array<TextureAtlas.AtlasRegion>> trLayers = new ArrayList<>();
        Array<TextureAtlas.AtlasRegion> region;

        for (int i = 0; i < layers; i++) {
            region = plantAtlas.findRegions(name + "layer" + i);
            trLayers.add(region);
        }

        return trLayers;
    }

    public String[] getTileList(texTypesNames ls) { return tileNames.get(ls); }

    public TextureRegion getTileTex(String name) {
        return tiles.get(name);
    }

    public String[] getRockList(texTypesNames ls) {
        return rockNames.get(ls);
    }

    public TextureRegion getRockTex(String name) {
        return rocks.get(name);
    }

    public Color getMossCol() {
        return fct.fromRGB(240, 240, 50);
    }

    public float getMossSDCol() {
        return 0.08f;
    }

    public treeType getTreeType(texTypesNames name) { return treeTypes.get(name); }
    public bushType getBushType(texTypesNames name) { return bushTypes.get(name); }

    public TextureRegion getWellTX(String name) { return wellAtlas.findRegion(name); }

    public void dispose() {
        plantAtlas.dispose();
        tileAtlas.dispose();
        rockAtlas.dispose();

        tiles.clear();
        rocks.clear();
    }

    private Texture createTreeShadow(int shadowSize) {
        Pixmap px = new Pixmap(shadowSize, shadowSize, Pixmap.Format.RGBA8888);

        //make a gradient half circle
        for (int x = 0; x < shadowSize; x++) {
            for (int y = 0; y < shadowSize; y++) {
                //oapcity is determined based on distance to edge and mapped to 0 - 1
                double val = fct.nearEdge(x, y, shadowSize / 2, 0);
                val = fct.map(val, 0, shadowSize / 2f, 1, 0);

                //make it a little darker
                val *= 1.2;

                //constrain to opacity vals
                val = fct.constrain(val, 0, 1);

                //draw pixel
                px.setColor(0, 0, 0, (float)val);
                px.drawPixel(x, y);
            }
        }
        return new Texture(px);
    }

    public Texture createSky() {
        Pixmap px = new Pixmap(cWIDTH, cHEIGHT, Pixmap.Format.RGBA8888);
        Color sCol = fct.fromRGB(60, 40, 60);
        Color eCol = fct.fromRGB(100, 80, 80);
        Color prevCol = sCol;

        //make a gradient half circle
        for (int y = 0; y < cHEIGHT; y++) {

            prevCol = fct.lerpCol(5f/(cHEIGHT), prevCol, eCol);

            for (int x = 0; x < cWIDTH; x++) {
                //draw pixel
                px.setColor(prevCol);
                px.drawPixel(x, y);
            }
        }
        return new Texture(px);
    }

    private void createTreeTypes() {
        Texture smallShadow = createTreeShadow(2 * TILESIZE);
        Texture mediumShadow = createTreeShadow(3 * TILESIZE);

        treeTypes.put(SMALLBUSHYTREE, new treeType(SMALLBUSHYTREE, new Vector2((TILESIZE / 2f / PPM), (128 / 2f / PPM)),
                fct.fromRGB(240, 80, 80), fct.fromRGB(235, 205, 175), 0.08f, 0.01f,
                new String[]{"small_1-1-1", "small_1-1-2"},
                smallShadow));

        treeTypes.put(MEDIUMBUSHYTREE, new treeType(MEDIUMBUSHYTREE, new Vector2(0, (256 / 2f / PPM)),
                fct.fromRGB(240, 80, 80), fct.fromRGB(235, 205, 175), 0.08f, 0.01f,
                new String[]{"medium_1-1-1", "medium_1-1-2", "medium_1-1-3", "medium_1-1-4", "medium_1-1-5", "medium_1-1-3_v2", "medium_1-1-5_v2"},
                mediumShadow));

        treeTypes.put(MEDIUMSPIKYTREE, new treeType(MEDIUMSPIKYTREE, new Vector2(0, (256 / 2f / PPM)),
                fct.fromRGB(255, 180, 80), fct.fromRGB(245, 116, 67), 0.05f, 0.004f,
                new String[]{"medium_1-2-1", "medium_1-2-2", "medium_1-2-3", "medium_1-2-1_v2"},
                mediumShadow));

        treeTypes.put(SMALLSPIKYTREE, new treeType(SMALLSPIKYTREE, new Vector2((TILESIZE / 2f / PPM), (128 / 2f / PPM)),
                fct.fromRGB(255, 180, 80), fct.fromRGB(245, 116, 67), 0.08f, 0.002f,
                new String[]{"small_1-2-1", "small_1-2-2", "small_1-2-1_v2", "small_1-2-2_v2"},
                smallShadow));

        treeTypes.put(COLOURFULTREE, new treeType(COLOURFULTREE, new Vector2(0, (256 / 2f / PPM)),
                fct.fromRGB(240, 90, 30), fct.fromRGB(240, 235, 235), 0.3f, 0.001f,
                new String[]{"front_1-3-1", "back_1-3-1"},
                smallShadow));
    }

    private void createBushTypes() {
        bushTypes.put(MEDIUMBUSH, new bushType(new Vector2(0, (24 / PPM)),
                fct.fromRGB(240, 240, 50), 0.08f,
                new String[]{"medium_1-1-1", "medium_1-1-2", "medium_1-1-3", "medium_1-1-4", "medium_1-1-5", "medium_1-1-6"}));
        bushTypes.put(SMALLBUSH, new bushType(new Vector2(0, (16 / PPM)),
                fct.fromRGB(240, 240, 50), 0.08f,
                new String[]{"small_1-1-1", "small_1-1-2", "small_1-1-3", "small_1-1-4", "small_1-1-5", "small_1-1-6", "small_1-1-7", "small_1-1-8", "small_1-1-9"}));
        bushTypes.put(LARGEBUSH, new bushType(new Vector2(TILESIZE / 2f / PPM, (40 / PPM)),
                fct.fromRGB(240, 240, 50), 0.08f,
                new String[]{"large_1-1-1", "large_1-1-2", "large_1-1-3"}));
    }

    private void loadRocks() {
        rockNames.put(SMALLROCK, new String[]{"rock-1-1", "rock-1-2", "rock-1-3", "rock-1-4", "rock-1-5", "rock-1-6", "rock-1-7", "rock-1-8"});
        rockNames.put(SMALLROCKMOSS, new String[]{"moss-1-1", "moss-1-2", "moss-1-3", "moss-1-4", "moss-1-5", "moss-1-6", "moss-1-7", "moss-1-8"});

        for (String[] listNames : rockNames.values()) {
            for (String name : listNames) {
                rocks.put(name, rockAtlas.findRegion(name));
            }
        }
    }

    private void loadTiles() {
        tileNames.put(TOPPER, new String[]{"grass-1-1", "grass-1-2", "grass-1-3"});
        tileNames.put(GROUND, new String[]{"ground-1-1", "ground-1-2", "groundSpeckle-1-1", "groundSpeckle-1-2"});
        tileNames.put(ROCKS, new String[]{"groundRock-1-1", "groundRockSpeckle-1-1"});
        tileNames.put(LIVEGROUND, new String[]{"groundLive-1-1", "groundLiveSpeckle-1-1"});
        tileNames.put(DOUBLELIVEGROUND, new String[]{"groundDLive-1-1", "groundDLiveSpeckle-1-1"});
        tileNames.put(SPECIALGROUND, new String[]{"groundSpecial-1-1", "groundSpecial-1-2", "groundSpecial-1-3"});

        for (String[] listNames : tileNames.values()) {
            for (String name : listNames) {
                tiles.put(name, tileAtlas.findRegion(name));
            }
        }
    }
}
