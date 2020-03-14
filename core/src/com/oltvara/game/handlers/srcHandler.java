package com.oltvara.game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;

public class srcHandler {

    private HashMap<String, Texture> textures;

    public srcHandler() {
        textures = new HashMap<String, Texture>();
    }

    public void importTX(String path, String key) {
        Texture tex = new Texture(Gdx.files.internal(path));
        textures.put(key, tex);
    }

    public Texture getTX(String key) {
        return textures.get(key);
    }

    public void disposeTexture(String key) {
        Texture tex = textures.get(key);
        if (tex != null) tex.dispose();
    }
}
