package org.cup;

import org.cup.assets.scenes.Tutorial;
import org.cup.engine.core.Game;

import java.io.File;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        String logo = System.getProperty("user.dir")
            + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + "org" + File.separator + "cup" + File.separator + "logo.png";
        Game game = new Game("Graphics Playground", 1280, 720, logo, 1f, false);

        Tutorial tutorial = new Tutorial();
        game.addScene(tutorial);
    }
}