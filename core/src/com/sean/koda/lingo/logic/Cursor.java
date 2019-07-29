package com.sean.koda.lingo.logic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sean.koda.lingo.internal.Resources;

public class Cursor {

    private Board board;
    private float alpha;
    private float renderX;
    private float renderY;
    private int row;
    private int col;

    public Cursor(Board board) {
        this.board = board;
        alpha = 1f;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setPosition(int row, int col) {
        //if (col == Board.WORD_LENGTH)
            //col--;

        renderX = board.getTileX(col);
        renderY = board.getTileY(row);

        this.row = row;
        this.col = col;
    }

    public void advance() {
        //if (col < Board.WORD_LENGTH - 1)
            col++;

        setPosition(row, col);
    }

    public void backspace() {
        if (col > 0)
            col--;

        setPosition(row, col);
    }

    public void render(SpriteBatch sb) {
        if (col >= Board.WORD_LENGTH)
            return;

        sb.begin();
        sb.draw(Resources.getTexture("cursor"), renderX, renderY, Board.TILE_PAD_SIZE, Board.TILE_PAD_SIZE);
        sb.end();
    }
}
