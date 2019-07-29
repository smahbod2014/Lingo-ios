package com.sean.koda.lingo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.sean.koda.lingo.internal.Resources;
import com.sean.koda.lingo.internal.StateManager;
import com.sean.koda.lingo.states.HighScoreState;
import com.sean.koda.lingo.states.MenuState;

public class Lingo extends ApplicationAdapter {

    public static final int SCREEN_WIDTH = 337;
    public static final int SCREEN_HEIGHT = 600;
    public static final int TILE_SIZE = 48;
    public static final boolean DEBUG = true;

	SpriteBatch batch;
    public static ShapeRenderer debugSr;
    static OrthographicCamera camera;
    static BitmapFont letterFont;
    static BitmapFont timerFont;
    static BitmapFont bonusFont;
    static BitmapFont markingFont;
    static PlatformAdapter platformAdapter;

	@Override
	public void create() {
		batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("calibri.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = 46;
        params.genMipMaps = true;
        params.magFilter = Texture.TextureFilter.Linear;
        params.minFilter = Texture.TextureFilter.Linear;
        letterFont = generator.generateFont(params);
        params.borderWidth = 1.25f;
        timerFont = generator.generateFont(params);
        params.size = 24;
        bonusFont = generator.generateFont(params);
        params.size = 36;
        params.borderColor = Color.BLACK;
        params.borderWidth = 1.5f;
        markingFont = generator.generateFont(params);
        generator.dispose();

        StateManager.addState(StateManager.MENU_STATE, new MenuState());
        StateManager.addState(StateManager.HIGH_SCORE_STATE, new HighScoreState());
        StateManager.setState(StateManager.MENU_STATE);

        debugSr = new ShapeRenderer();
        debugSr.setProjectionMatrix(camera.combined);

        Resources.loadTexture("blank", "tiles_" + TILE_SIZE + "/BlankTile_" + TILE_SIZE + ".png");
        Resources.loadTexture("cursor", "cursor_48.png");
        Resources.loadTexture("correct", "correct_48.png");
        Resources.loadTexture("wrong", "wrong_48.png");
        Resources.loadTexture("bonus", "bonus_48.png");
        Resources.loadTexture("invalid", "invalid_48.png");
        Resources.getTexture("blank").setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

	@Override
	public void render() {
		Gdx.gl.glClearColor(.9f, .9f, .9f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        StateManager.update(Gdx.graphics.getDeltaTime());
        StateManager.render(batch);
    }

    @Override
    public void resume() {
        StateManager.resume();
    }

    @Override
    public void pause() {
        StateManager.pause();
    }

    @Override
    public void resize(int width, int height) {
        StateManager.resize(width, height);
    }

    @Override
    public void dispose() {
        StateManager.disposeAll();
        batch.dispose();
        letterFont.dispose();
        timerFont.dispose();
        bonusFont.dispose();
        markingFont.dispose();
        debugSr.dispose();
        Resources.dispose();
    }

    public static void log(String message) {
        Gdx.app.log("Lingo", message);
    }

    public static void unproject(Vector3 coords) {
        camera.unproject(coords);
    }

    public static Vector2 getTouchCoords() {
        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f);
        Lingo.unproject(mouse);
        return new Vector2(mouse.x, mouse.y);
    }

    public static BitmapFont getLetterFont() {
        return letterFont;
    }

    public static BitmapFont getTimerFont() {
        return timerFont;
    }

    public static BitmapFont getBonusFont() {
        return bonusFont;
    }

    public static BitmapFont getMarkingFont() {
        return markingFont;
    }

    public static int rand(int range) {
        return (int) (Math.random() * range);
    }

    public void setPlatformAdapter(PlatformAdapter adapter) {
        Lingo.platformAdapter = adapter;
    }

    public static void toast(String message) {
        platformAdapter.displayMessage(message);
    }
}
