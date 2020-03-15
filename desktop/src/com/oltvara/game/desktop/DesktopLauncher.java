package com.oltvara.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.oltvara.game.mainGame;

import static com.oltvara.game.mainGame.*;

public class DesktopLauncher {

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = TITLE;
		config.width = cWIDTH * SCALE;
		config.height = cHEIGHT * SCALE;

		new LwjglApplication(new mainGame(), config);
	}
}
