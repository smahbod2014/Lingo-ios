package com.sean.koda.lingo.internal;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.HashMap;

public class StateManager {

    public static final int MENU_STATE = 0;
    public static final int PLAY_STATE = 1;
    public static final int HIGH_SCORE_STATE = 2;

    private static HashMap<Integer, GameState> allStates = new HashMap<Integer, GameState>();
    private static GameState gameState;

    public static GameState getGameState(int code) {
        return allStates.get(code);
    }

    public static void addState(int code, GameState state) {
        allStates.put(code, state);
    }

    public static void setState(GameState state) {
        StateManager.gameState = state;
        gameState.show();
    }

    public static void setState(int code) {
        if (allStates.get(code) == null) {
            System.out.println("Code does not exist");
            return;
        }

        setState(allStates.get(code));
    }

    public static void update(float dt) {
        gameState.update(dt);
    }

    public static void render(SpriteBatch sb) {
        gameState.render(sb);
    }

    public static void pause() {
        gameState.pause();
    }

    public static void resume() {
        gameState.resume();
    }

    public static void resize(int width, int height) {
        gameState.resize(width, height);
    }

    public static void dispose() {
        gameState.dispose();
    }

    public static void disposeAll() {
        for (GameState gs : allStates.values()) {
            gs.dispose();
        }
    }
}
