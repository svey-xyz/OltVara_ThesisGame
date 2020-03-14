package com.oltvara.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.oltvara.game.mainGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = mainGame.TITLE;
		config.width = mainGame.cWIDTH * mainGame.SCALE;
		config.height = mainGame.cHEIGHT * mainGame.SCALE;

		new LwjglApplication(new mainGame(), config);
	}
}
