package com.sean.koda.lingo.internal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.sean.koda.lingo.states.PlayState;

public class MyInputProcessor extends InputAdapter {

    PlayState playState;

    public MyInputProcessor(PlayState playState) {
        this.playState = playState;
    }

    @Override
    public boolean keyDown(int keycode) {
        Gdx.app.log("MyInputProcessor", "Key down: " + keycode);
        if (keycode == Input.Keys.BACK) {
            StateManager.setState(StateManager.MENU_STATE);
            return true;
        }

        playState.typeKey(keycode);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        Gdx.app.log("MyInputProcessor", "Key up: " + keycode);
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        Gdx.app.log("MyInputProcessor", "Key typed: " + character);
        int keycode;
        if (character == 8) {
            keycode = Input.Keys.BACKSPACE;
        }
        else if (character == 46) {
            keycode = Input.Keys.PERIOD;
        }
        else if (character == 44) {
            keycode = Input.Keys.COMMA;
        }
        else if (character == 66) {
            keycode = Input.Keys.ENTER;
        }
        else {
            String s = Character.toString(character).toLowerCase();
            keycode = s.charAt(0) - 68;
        }
        playState.typeKey(keycode);
        return true;
    }
}
