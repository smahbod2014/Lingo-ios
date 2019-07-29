package com.sean.koda.lingo.internal;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;

public class Utils {

    private static final GlyphLayout layout = new GlyphLayout();

    public static Vector2 getDimensions(BitmapFont font, String text) {
        layout.setText(font, text);
        return new Vector2(layout.width, layout.height);
    }
}
