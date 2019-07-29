package com.sean.koda.lingo.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.sean.koda.lingo.Lingo;
import com.sean.koda.lingo.internal.GameState;
import com.sean.koda.lingo.internal.StateManager;

public class MenuState extends GameState {

    public enum MenuMode { MAIN_MENU, PLAY_MODE_SELECTION, SETTINGS_MENU, GAME_OVER }
    public enum Obscurity {
        EASY(1250), NORMAL(3000), HARD(-1);
        public int limit;
        private Obscurity(int limit) {
            this.limit = limit;
        }
    }

    //TODO: This batch and camera are for the Lingo title, not the UI
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Skin skin;
    private Stage stage;
    private Table table;
    private CheckBox easyWords;
    private CheckBox normalWords;
    private CheckBox allWords;
    private ButtonGroup<CheckBox> checkBoxButtonGroup;

    private Obscurity obscurity = Obscurity.EASY;
    private String word;
    private int score;

    public MenuState() {
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        skin.addRegions(new TextureAtlas(Gdx.files.internal("ui/uiskin.atlas")));
        stage = new Stage(new ScalingViewport(Scaling.fit, Lingo.SCREEN_WIDTH, Lingo.SCREEN_HEIGHT));
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Lingo.SCREEN_WIDTH, Lingo.SCREEN_HEIGHT);
        camera.update();
        table = new Table();
        easyWords = new CheckBox("Easy words only", skin);
        normalWords = new CheckBox("Some tough words",skin);
        allWords = new CheckBox("All words", skin);
        checkBoxButtonGroup = new ButtonGroup<CheckBox>();
        checkBoxButtonGroup.add(easyWords);
        checkBoxButtonGroup.add(normalWords);
        checkBoxButtonGroup.add(allWords);

        stage.addActor(table);

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);

        setup(MenuMode.MAIN_MENU);
    }

    public void setEndGameStats(String word, int score) {
        this.word = word;
        this.score = score;
    }

    public void setup(MenuMode mode) {
        TextButton menuButton = new TextButton("Main Menu", skin);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setup(MenuMode.MAIN_MENU);
                getObscurity();
            }
        });

        switch (mode) {
            case MAIN_MENU:
                table.clear();

                TextButton playButton = new TextButton("Play", skin);
                TextButton highScoreButton = new TextButton("High Scores", skin);
                TextButton settingsButton = new TextButton("Settings", skin);
                TextButton quitButton = new TextButton("Quit", skin);

                table.setFillParent(true);
                table.center();
                table.add(playButton).padBottom(10).colspan(10).fill().row();
                table.add(highScoreButton).padBottom(10).colspan(10).fill().row();
                table.add(settingsButton).padBottom(10).colspan(10).fill().row();
                table.add(quitButton).colspan(10).fill();

                //the listeners
                playButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        //Lingo.log("Play button clicked");
                        //StateManager.setState(StateManager.PLAY_STATE);
                        setup(MenuMode.PLAY_MODE_SELECTION);
                    }
                });

                highScoreButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        //Lingo.log("High Score button clicked");
                        Lingo.toast("Coming soon!");
                    }
                });

                settingsButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        setup(MenuMode.SETTINGS_MENU);
                    }
                });

                quitButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Gdx.app.exit();
                    }
                });
                break;
            case PLAY_MODE_SELECTION:
                table.clear();

                TextButton regularButton = new TextButton("Regular", skin);
                TextButton regularMthnButton = new TextButton("Marathon", skin);
                TextButton hardButton = new TextButton("Hard Mode", skin);
                TextButton hardMthnButton = new TextButton("Hard Mode Marathon", skin);
                table.setFillParent(true);
                table.center();
                table.add(regularButton).padBottom(10).colspan(10).fill().row();
                table.add(regularMthnButton).padBottom(10).colspan(10).fill().row();
                table.add(hardButton).padBottom(10).colspan(10).fill().row();
                table.add(hardMthnButton).padBottom(10).colspan(10).fill().row();
                table.add(menuButton).colspan(10).fill();

                regularButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        StateManager.setState(new PlayState(PlayState.PlayMode.REGULAR, getObscurity()));
                    }
                });

                regularMthnButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        StateManager.setState(new PlayState(PlayState.PlayMode.MARATHON, getObscurity()));
                    }
                });

                hardButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        StateManager.setState(new PlayState(PlayState.PlayMode.HARD, getObscurity()));
                    }
                });

                hardMthnButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        StateManager.setState(new PlayState(PlayState.PlayMode.HARD_MARATHON, getObscurity()));
                    }
                });
                break;
            case SETTINGS_MENU:
                table.clear();

                Label label = new Label("Select word obscurity", skin);
                //Label.LabelStyle style = new Label.LabelStyle(new BitmapFont(), Color.RED);
                //label.setStyle(style);
                //label.setScale(2f);

                if (obscurity == Obscurity.EASY) {
                    easyWords.setChecked(true);
                }
                else if (obscurity == Obscurity.NORMAL)
                    normalWords.setChecked(true);
                else {
                    Lingo.log("obscurity was hard");
                    allWords.setChecked(true);
                }

                table.setFillParent(true);
                table.center();
                table.add(label).padBottom(50).row();
                table.add(easyWords).align(Align.left).padBottom(10).colspan(10).row();
                table.add(normalWords).align(Align.left).padBottom(10).colspan(10).row();
                table.add(allWords).align(Align.left).padBottom(10).colspan(10).row();
                table.add(menuButton).colspan(10).fill();
                break;
            case GAME_OVER:
                table.clear();
                Label l1 = new Label("Time's up!", skin);
                Label l2 = new Label("The last word was: " + word, skin);
                Label l3 = new Label("Your score was: " + score, skin);

                table.setFillParent(true);
                table.center();
                table.add(l1).padBottom(20).row();
                table.add(l2).padBottom(20).row();
                table.add(l3).padBottom(20).row();
                table.add(menuButton).colspan(10).fill();

                Gdx.input.setOnscreenKeyboardVisible(false);
        }
    }

    private Obscurity getObscurity() {
        if (easyWords.isChecked()) {
            obscurity = Obscurity.EASY;
        }
        else if (normalWords.isChecked()) {
            obscurity = Obscurity.NORMAL;
        }
        else {
            obscurity = Obscurity.HARD;
        }
        return obscurity;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(false);
    }

    @Override
    public void update(float dt) {
        stage.act(dt);
    }

    @Override
    public void render(SpriteBatch sb) {
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void dispose() {
        skin.dispose();
        stage.dispose();
        batch.dispose();
    }
}
