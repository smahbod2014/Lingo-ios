package com.sean.koda.lingo.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sean.koda.lingo.Lingo;
import com.sean.koda.lingo.internal.GameState;
import com.sean.koda.lingo.internal.MyInputProcessor;
import com.sean.koda.lingo.internal.StateManager;
import com.sean.koda.lingo.internal.Timer;
import com.sean.koda.lingo.logic.Board;

public class PlayState extends GameState {

    public enum PlayMode { REGULAR, MARATHON, HARD, HARD_MARATHON;
        public boolean isMarathon() {
            return this == MARATHON || this == HARD_MARATHON;
        }

        public boolean isHardMode() {
            return this == HARD || this == HARD_MARATHON;
        }
    }

    public static final long VICTORY_DELAY = 1000;
    public static final long DEFEAT_DELAY = 3000;
    public static final int BONUS_LETTERS = 3;
    public static final int MARATHON_TIME = 180;
    public static String[] wordBank;
    public static String[] dictionary;
    {
        long now = System.currentTimeMillis();
        FileHandle file = Gdx.files.internal("wordlist.txt");
        dictionary = file.readString().replaceAll("[\\t\\n\\r]+", " ").split(" ");
        file = Gdx.files.internal("wordbank.txt");
        wordBank = file.readString().replaceAll("[\\t\\n\\r]+", " ").split(" ");
        long elapsed = System.currentTimeMillis() - now;
        Lingo.log("[PlayState] Took " + elapsed / 1000.0 + " seconds to read the word bank and dictionary");
    }

    Board board;
    boolean victoryMode = false;
    int limit;
    PlayMode mode;

    //marathon modes
    private int bonusLetters;
    private int time;
    private int marathonScore;

    public PlayState(PlayMode mode, MenuState.Obscurity obscurity) {
        Lingo.log("PlayState entered. " + mode.toString() + " mode selected. Obscurity is " + obscurity.toString());
        limit = obscurity.limit;
        if (limit == -1)
            limit = wordBank.length;
        board = new Board(5, Board.PADDING, 235, mode);
        board.setTargetWord(wordBank[Lingo.rand(limit)]);
        this.mode = mode;

        if (mode.isMarathon()) {
            bonusLetters = BONUS_LETTERS;
            time = MARATHON_TIME;
            Timer.start("marathon_timer", 1000);
        }

        if (Gdx.input.isPeripheralAvailable(Input.Peripheral.OnscreenKeyboard)) {
            Gdx.app.log("PlayState", "OnscreenKeyboard is available");
        }
        else {
            Gdx.app.log("PlayState", "OnscreenKeyboard is NOT available");
        }
    }

    public void typeKey(int key) {
        if (Timer.running("victory") || Timer.running("defeat"))
            return;

        if (key >= Input.Keys.A && key <= Input.Keys.Z && board.getColumn() < Board.WORD_LENGTH)
            board.addLetter("" + (char) (key + 36));
        else if (key == Input.Keys.BACKSPACE && board.getColumn() > 0)
            board.removeLast();
        else if (key == Input.Keys.ENTER) {
            Gdx.input.setOnscreenKeyboardVisible(true);
            Board.BoardState boardState = board.submitGuess();
            if (boardState == Board.BoardState.BOARD_VICTORY) {
                Timer.start("victory", VICTORY_DELAY);
                victoryMode = true;
                if (mode.isMarathon())
                    marathonScore++;
            } else if (boardState == Board.BoardState.BOARD_DEFEAT) {
                Timer.start("defeat", DEFEAT_DELAY);
            }
        }
        else if (mode.isMarathon() && key == Input.Keys.COMMA && bonusLetters > 0) {
            if (board.doBonusLetter())
                bonusLetters--;
        } else if (key == Input.Keys.PERIOD) {
            board.resetRow();
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new MyInputProcessor(this));
        Gdx.input.setOnscreenKeyboardVisible(true);
        Gdx.input.setCatchBackKey(true);
        Gdx.app.log("PlayState", "Setting InputProcessor");
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.justTouched()) {
            Gdx.app.log("PlayState", "Touched at: " + Lingo.getTouchCoords());
            Gdx.input.setOnscreenKeyboardVisible(true);
            Gdx.input.setOnscreenKeyboardVisible(false);
            Gdx.input.setOnscreenKeyboardVisible(true);
        }

        board.update(dt);

        if (Timer.justFinished("victory") || Timer.justFinished("defeat")) {
            victoryMode = false;
            board.reset();
            //TODO: Add bias against words farther down the list
            board.setTargetWord(wordBank[Lingo.rand(limit)]);
            //board.initializeRow();
        }

        if (mode.isMarathon()) {
            if (Timer.justFinished("marathon_timer")) {
                Timer.start("marathon_timer", 1000);
                time--;
                if (time < 0) {
                    MenuState menuState = (MenuState) StateManager.getGameState(StateManager.MENU_STATE);
                    menuState.setEndGameStats(board.getTargetWord(), marathonScore);
                    menuState.setup(MenuState.MenuMode.GAME_OVER);
                    StateManager.setState(menuState);
                }
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        board.render(sb);

        if (Timer.running("defeat")) {
            sb.begin();
            Lingo.getBonusFont().setColor(Color.RED);
            Lingo.getBonusFont().draw(sb, board.getTargetWord(), 10, 590);
            Lingo.getBonusFont().setColor(Color.WHITE);
            sb.end();
        }

        if (Timer.running("victory")) {
            sb.begin();
            Lingo.getBonusFont().setColor(Color.RED);
            Lingo.getBonusFont().draw(sb, "Nice!", 10, 590);
            Lingo.getBonusFont().setColor(Color.WHITE);
            sb.end();
        }

        if (mode.isMarathon()) {
            String timeText = convertTime();
            sb.begin();
            BitmapFont f = Lingo.getTimerFont();
            if (time <= 30 && time % 2 == 0)
                f.setColor(Color.RED);
            else
                f.setColor(Color.WHITE);
            f.draw(sb, timeText, 250, 575);

            f = Lingo.getBonusFont();
            String bonusText = "Bonus\nLetters: ";
            f.draw(sb, bonusText, 125, 585);
            f.draw(sb, "Score: " + marathonScore, 10, 550);

            f = Lingo.getTimerFont();
            f.setColor(Color.WHITE);
            f.draw(sb, Integer.toString(bonusLetters), 210, 570);
            sb.end();
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void pause() {
        //Lingo.log("Play state paused");
    }

    @Override
    public void resume() {
        //Lingo.log("Play state resumed");
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    private String convertTime() {
        int minutes = time / 60;
        int seconds = time % 60;
        String s = "" + minutes +":";
        if (seconds < 10)
            s += "0";
        s += seconds;
        return s;
    }
}
