package com.sean.koda.lingo.internal;

import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;

public class Resources {

    private static final HashMap<String, Texture> textures = new HashMap<String, Texture>();

    private Resources() {}

    public static void loadTexture(String alias, String filename) {
        textures.put(alias, new Texture(filename));
    }

    public static Texture getTexture(String alias) {
        return textures.get(alias);
    }

    public static void dispose() {
        for (Texture t : textures.values())
            t.dispose();

        textures.clear();
    }
}
