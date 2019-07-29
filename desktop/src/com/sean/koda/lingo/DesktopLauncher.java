package com.sean.koda.lingo;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher implements PlatformAdapter {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Lingo.SCREEN_WIDTH;
        config.height = Lingo.SCREEN_HEIGHT;
        config.title = "Lingo";
        config.resizable = false;
        config.addIcon("Tile_32_L.png", Files.FileType.Internal);
        Lingo lingo = new Lingo();
        lingo.setPlatformAdapter(new DesktopLauncher());
		new LwjglApplication(lingo, config);
	}

    @Override
    public void displayMessage(String message) {
        Gdx.app.log("DesktopLauncher", message);
    }
}
