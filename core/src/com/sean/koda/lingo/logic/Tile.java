package com.sean.koda.lingo.logic;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sean.koda.lingo.Lingo;
import com.sean.koda.lingo.internal.Resources;

public class Tile {

    public enum Mark { NONE, CORRECT, WRONG, BONUS, INVALID }

    private static GlyphLayout layout = new GlyphLayout();

    private Board board;
    private String value;
    private float renderX;
    private float renderY;
    private float width;
    private float height;
    private Mark mark;
    private int row;
    private int col;

    public Tile(String value, Board board) {
        this.value = value;
        this.board = board;
        renderX = board.getX() + board.getColumn() * Board.TILE_PAD_SIZE + Board.PADDING;
        renderY = board.getY() + board.getHeight() - Board.TILE_PAD_SIZE * (board.getRow() + 1) + Board.PADDING;

        layout.setText(Lingo.getLetterFont(), value);
        width = layout.width;
        height = layout.height;
        mark = Mark.NONE;

        row = board.getRow();
        col = board.getColumn();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        layout.setText(Lingo.getLetterFont(), value);
        width = layout.width;
        height = layout.height;
    }

    public Mark getMark() {
        return mark;
    }

    public void setMark(Mark type) {
        mark = type;
    }

    public void render(SpriteBatch sb) {
        sb.begin();
        float x = renderX;
        float y = renderY;
        sb.draw(Resources.getTexture("blank"), x, y);
        Lingo.getLetterFont().draw(sb, value, x + Lingo.TILE_SIZE / 2 - width / 2, y + Lingo.TILE_SIZE / 2 + height / 2);

        switch (mark) {
            case NONE: break;
            case CORRECT:
                sb.draw(Resources.getTexture("correct"), board.getTileX(col), board.getTileY(row), Board.TILE_PAD_SIZE, Board.TILE_PAD_SIZE);
                break;
            case WRONG:
                sb.draw(Resources.getTexture("wrong"), board.getTileX(col), board.getTileY(row), Board.TILE_PAD_SIZE, Board.TILE_PAD_SIZE);
                break;
            case BONUS:
                sb.draw(Resources.getTexture("bonus"), board.getTileX(col), board.getTileY(row), Board.TILE_PAD_SIZE, Board.TILE_PAD_SIZE);
                break;
            case INVALID:
                sb.draw(Resources.getTexture("invalid"), board.getTileX(col), board.getTileY(row), Board.TILE_PAD_SIZE, Board.TILE_PAD_SIZE);
                break;
        }

        sb.end();
    }
}
