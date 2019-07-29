package com.sean.koda.lingo.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.sean.koda.lingo.Lingo;
import com.sean.koda.lingo.states.PlayState;

import java.util.ArrayList;
import java.util.HashMap;

public class Board {

    public static final int WORD_LENGTH = 5;
    public static final float PADDING = Lingo.TILE_SIZE / 10f;
    public static final float TILE_PAD_SIZE = Lingo.TILE_SIZE + PADDING * 2;

    public enum BoardState { BOARD_VICTORY, BOARD_INCORRECT, BOARD_INVALID_GUESS, BOARD_DEFEAT }

    private ArrayList<Tile> tiles;
    private Cursor cursor;
    private int rows;
    private int currentRow;
    private int currentColumn;
    private String currentWord;
    private float renderX;
    private float renderY;
    private PlayState.PlayMode mode;
    private ArrayList<Integer> grantedPositions;

    //hard mode variables
    private ArrayList<Marking> markings = new ArrayList<Marking>();

    public Board(int rows, float x, float y, PlayState.PlayMode mode) {
        this.rows = rows;
        tiles = new ArrayList<Tile>();
        grantedPositions = new ArrayList<Integer>();
        renderX = x;
        renderY = y;
        currentRow = 0;
        cursor = new Cursor(this);
        currentWord = "";
        this.mode = mode;
    }

    public void update(float dt) {
        if (Gdx.input.justTouched()) {
            Vector2 coords = Lingo.getTouchCoords();
            coords.x -= renderX;
            coords.y -= renderY;
            int row = rows - (int) (coords.y / TILE_PAD_SIZE) - 1;
            int col = (int) (coords.x / TILE_PAD_SIZE);
            if (row == currentRow && col > 0 && col < WORD_LENGTH)
                cursor.setPosition(row, col);
        }
    }

    public void reset() {
        tiles.clear();
        currentColumn = 0;
        currentRow = 0;
    }

    public void setTargetWord(String word) {
        word = word.toUpperCase();
        currentWord = word;
        //Lingo.log("[Board] Word was set to: " + currentWord);

        if (mode.isMarathon()) {
            grantedPositions.clear();
            grantedPositions.add(Lingo.rand(4) + 1);
            //Lingo.log("Granted position is " + grantedPosition);
        }

        if (mode.isHardMode()) {
            markings.clear();
        }

        initializeRow();
    }

    public String getTargetWord() {
        return currentWord;
    }

    private Tile anyCorrectInColumn(int col) {
        for (int i = 0; i < currentRow; i++) {
            Tile t = getTile(i, col);
            if (t.getMark() == Tile.Mark.CORRECT)
                return t;
        }
        return null;
    }

    public void initializeRow() {
        int startingColumn = 1;
        boolean startColumnSet = false;
        for (int i = 0; i < WORD_LENGTH; i++) {
            currentColumn = i;
            Tile t = new Tile("", this);
            tiles.add(t);
            Tile test = anyCorrectInColumn(i);
            if (i == 0)
                t.setValue("" + currentWord.charAt(0));
            else if (test != null)
                t.setValue(test.getValue());
            else {
                t.setValue(".");
                if (!startColumnSet) {
                    startingColumn = i;
                    startColumnSet = true;
                }
            }
        }

        if (mode.isMarathon()) {
            for (Integer i : grantedPositions) {
                getTile(currentRow, i).setValue("" + currentWord.charAt(i));

                /*if (i <= startingColumn) {
                    startingColumn = i + 1;
                }*/
            }
        }

        //currentColumn = startingColumn;
        //cursor.setPosition(currentRow, currentColumn);
        advanceCursorFarthest();
    }

    public boolean doBonusLetter() {
        int numCorrect = 0;
        for (int i = 1; i < WORD_LENGTH; i++)
            if (anyCorrectInColumn(i) != null || isGranted(i))
                numCorrect++;

        Lingo.log("numCorrect = " + numCorrect);
        if (numCorrect >= WORD_LENGTH - 1)
            return false;

        resetRow();

        for (int i = 1; i < WORD_LENGTH; i++) {
            Tile t = getTile(currentRow, i);
            Lingo.log("Attempting to assign bonus letter to column " + i);
            if (canAssignBonusLetter(t, i)) {
                t.setValue("" + currentWord.charAt(i));
                t.setMark(Tile.Mark.BONUS);
                grantedPositions.add(i);
                advanceCursorFarthest();
                Lingo.log("Successfully assigned bonus letter to column " + i);
                return true;
            }
        }

        Lingo.log("Failed to apply bonus letter");
        //failed to assign a bonus letter
        return false;
    }

    private boolean canAssignBonusLetter(Tile t, int col) {
        //has a bonus letter already been assigned in this column?
        if (isGranted(col)) {
            Lingo.log("Already have something granted in column " + col);
            return false;
        }

        //have we already gotten a correct tile in this column?
        if (mode == PlayState.PlayMode.MARATHON && anyCorrectInColumn(col) != null) {
            Lingo.log("Already have a correct tile in column " + col);
            return false;
        }

        if (t.getValue().equals("."))
            Lingo.log("Success, can assign bonus letter to column " + col);

        return t.getValue().equals(".");
    }

    public void resetRow() {
        for (int i = 1; i < WORD_LENGTH; i++) {
            Tile t = anyCorrectInColumn(i);
            if (t != null) {
                getTile(currentRow, i).setValue(t.getValue());
            }
            else if (isGranted(i)) {
                getTile(currentRow, i).setValue("" + currentWord.charAt(i));
            }
            else {
                getTile(currentRow, i).setValue(".");
            }
        }

        for (int i = 1; i < grantedPositions.size(); i++) {
            getTile(currentRow, grantedPositions.get(i)).setMark(Tile.Mark.BONUS);
        }

        advanceCursorFarthest();
    }

    private boolean isGranted(int position) {
        for (int i = 0; i < grantedPositions.size(); i++) {
            if (grantedPositions.get(i) == position) {
                return true;
            }
        }
        return false;
    }

    public void addLetter(String letter) {
        if (cursor.getCol() == WORD_LENGTH)
            return;

        Tile t = getTile(cursor.getRow(), cursor.getCol());
        t.setValue(letter);
        cursor.advance();
    }

    public void removeLast() {
        if (cursor.getCol() > 1)
            cursor.backspace();

        Tile t = getTile(cursor.getRow(), cursor.getCol());
        t.setValue(".");
        if (t.getMark() == Tile.Mark.BONUS)
            t.setMark(Tile.Mark.NONE);
    }

    public void advanceCursorFarthest() {
        for (int i = 1; i < WORD_LENGTH; i++) {
            Tile t = getTile(currentRow, i);
            if (t.getValue().equals(".")) {
                currentColumn = i;
                cursor.setPosition(currentRow, currentColumn);
                Lingo.log("Setting cursor to column " + i);
                return;
            }
        }
    }

    public void advanceRow() {
        currentRow++;
        currentColumn = 0;
    }

    public String getWord() {
        String result = "";
        int offset = currentRow * WORD_LENGTH;
        for (int i = offset; i < offset + WORD_LENGTH; i++) {
            result += tiles.get(i).getValue();
        }
        return result;
    }

    public BoardState submitGuess() {
        for (int i = 0; i < WORD_LENGTH; i++) {
            String letter = getTile(currentRow, i).getValue();
            if (letter.equals(".")) {
                return BoardState.BOARD_INVALID_GUESS;
            }
        }

        String submission = getWord();
        evaluateWord(submission);
        if (submission.equalsIgnoreCase(currentWord)) {
            return BoardState.BOARD_VICTORY;
        }

        advanceRow();
        if (currentRow == rows)
            return BoardState.BOARD_DEFEAT;

        initializeRow();
        return BoardState.BOARD_INCORRECT;
    }

    private boolean isInDictionary(String word) {
        for (int i = 0; i < PlayState.dictionary.length; i++) {
            if (PlayState.dictionary[i].equalsIgnoreCase(word))
                return true;
        }

        //sometimes words can be in the word bank but not the dictionary, like "Gonna", but they should still be valid
        for (int i = 0; i < PlayState.wordBank.length; i++) {
            if (PlayState.wordBank[i].equalsIgnoreCase(word))
                return true;
        }

        return false;
    }

    public void evaluateWord(String word) {
        if (!isInDictionary(word)) {
            for (int i = 0; i < WORD_LENGTH; i++) {
                getTile(currentRow, i).setMark(Tile.Mark.INVALID);
            }
            return;
        }

        word = word.substring(1).toLowerCase();
        String target = currentWord.substring(1).toLowerCase();

        //check for letters that are correct AND in the right spot
        int colPosition = 1;
        for (int i = 0; i < word.length(); i++) {
            char a = word.charAt(i);
            char b = target.charAt(i);
            if (a == b) {
                Tile t = getTile(currentRow, colPosition);
                t.setMark(Tile.Mark.CORRECT);

                word = word.substring(0, i) + word.substring(i + 1);
                target = target.substring(0, i) + target.substring(i + 1);
                i--;
            }

            colPosition++;
        }

        //check for letters that are correct but in the wrong spot
        /*gets the number of occurrences of each letter
        *ex: a: 2, s: 1...
        */
        HashMap<Character, Integer> charMap = new HashMap<Character, Integer>();
        HashMap<Character, Integer> targetCharMap = new HashMap<Character, Integer>();
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (charMap.get(c) == null) {
                charMap.put(c, 1);
            } else {
                charMap.put(c, charMap.get(c) + 1);
            }

            c = target.charAt(i);
            if (targetCharMap.get(c) == null) {
                targetCharMap.put(c, 1);
            } else {
                targetCharMap.put(c, targetCharMap.get(c) + 1);
            }
        }

        for (int i = 1; i < WORD_LENGTH; i++) {
            Tile t = getTile(currentRow, i);
            if (t.getMark() != Tile.Mark.NONE)
                continue;

            //TODO: make Tile's value a char instead of a String
            char c = t.getValue().toLowerCase().charAt(0);
            if (charMap.get(c) == null)
                continue;

            int count = charMap.get(c);
            if (targetCharMap.get(c) != null) {
                t.setMark(Tile.Mark.WRONG);
                int remaining = targetCharMap.get(c);
                if (remaining == 1)
                    targetCharMap.remove(c);
                else
                    targetCharMap.put(c, remaining - 1);

                if (count == 1) {
                    charMap.remove(c);
                } else {
                    charMap.put(c, count - 1);
                }
            } else {
                charMap.remove(c);
            }
        }

        if (mode.isHardMode()) {
            int numRight = 0;
            int numWrong = 0;

            for (int i = 1; i < WORD_LENGTH; i++) {
                Tile t = getTile(currentRow, i);
                if (t.getMark() == Tile.Mark.WRONG) {
                    numWrong++;
                }
                if (t.getMark() == Tile.Mark.CORRECT) {
                    numRight++;
                }
                t.setMark(Tile.Mark.NONE);
            }

            markings.add(new Marking(numWrong, numRight, getTileX(WORD_LENGTH - 1) + TILE_PAD_SIZE + 2f,
                    getTileY(currentRow) + TILE_PAD_SIZE * 0.75f));

            for (int i = 1; i < grantedPositions.size(); i++) {
                if (getTile(currentRow, grantedPositions.get(i)).getValue().equals("" + currentWord.charAt(grantedPositions.get(i))))
                    getTile(currentRow, grantedPositions.get(i)).setMark(Tile.Mark.BONUS);
            }
        }
    }

    public void render(SpriteBatch sb) {
        if (Lingo.DEBUG) {
            Lingo.debugSr.setColor(Color.BLACK);
            Lingo.debugSr.begin(ShapeRenderer.ShapeType.Line);

            for (int i = 0; i < 6; i++) {
                float x = renderX + TILE_PAD_SIZE * i;
                Lingo.debugSr.line(x, renderY, x, getHeight() + renderY);
            }

            for (int i = 0; i < 6; i++) {
                float y = i * TILE_PAD_SIZE + renderY;
                Lingo.debugSr.line(renderX, y, WORD_LENGTH * TILE_PAD_SIZE + renderX, y);
            }

            Lingo.debugSr.end();
        }

        for (Tile t : tiles) {
            t.render(sb);
        }

        cursor.render(sb);

        if (mode.isHardMode()) {
            for (Marking m : markings) {
                m.render(sb);
            }
        }
    }

    public float getX() {
        return renderX;
    }

    public float getY() {
        return renderY;
    }

    public int getRow() {
        return currentRow;
    }

    public int getColumn() {
        return currentColumn;
    }

    public float getHeight() {
        return rows * TILE_PAD_SIZE;
    }

    public Tile getTile(int row, int col) {
        int index = row * WORD_LENGTH + col;
        return tiles.get(index);
    }

    public float getTileX(int col) {
        return renderX + col * TILE_PAD_SIZE;
    }

    public float getTileY(int row) {
        return renderY + (rows - row - 1) * TILE_PAD_SIZE;
    }
}
