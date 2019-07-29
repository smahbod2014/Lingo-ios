package com.sean.koda.lingo.logic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.sean.koda.lingo.Lingo;
import com.sean.koda.lingo.internal.Utils;

public class Marking {

    public float x;
    public float y;
    public int numWrong;
    public int numRight;

    public Marking(int numWrong, int numRight, float x, float y) {
        this.x = x;
        this.y = y;
        this.numWrong = numWrong;
        this.numRight = numRight;
    }

    public void render(SpriteBatch sb) {
        BitmapFont f = Lingo.getMarkingFont();
        sb.begin();
        String text = "" + numWrong + " ";
        Vector2 dims = Utils.getDimensions(f, text);
        f.setColor(new Color(214 / 255f, 214 / 255f, 0, 1));
        f.draw(sb, text, x, y);
        f.setColor(Color.RED);
        f.draw(sb, "" + numRight, x + dims.x - 5f, y);
        sb.end();
    }
}
