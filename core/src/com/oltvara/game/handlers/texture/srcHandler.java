package com.oltvara.game.handlers.texture;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class srcHandler {

    private HashMap<String, Texture> textures;
    private HashMap<String, TextureAtlas> txAtlas;

    public srcHandler() {
        textures = new HashMap<String, Texture>();
        txAtlas = new HashMap<String, TextureAtlas>();
    }

    public void importTX(String path, String key) {
        Texture tex = new Texture(Gdx.files.internal(path));
        textures.put(key, tex);
    }

    public void importAtlas(String path, String key) {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(path));
        txAtlas.put(key, atlas);
    }

    public Texture getTX(String key) {
        return textures.get(key);
    }
    public TextureAtlas getTXAtlas(String key) {
        return txAtlas.get(key);
    }
    public Array<TextureAtlas.AtlasRegion> getTXfromAtlas(String atlas, String regionName)  { return txAtlas.get(atlas).findRegions(regionName); }

    public void disposeTexture(String key) {
        Texture tex = textures.get(key);
        if (tex != null) tex.dispose();
    }
}
