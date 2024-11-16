package org.cup.assets.objects;

import org.cup.assets.PathHelper;
import org.cup.engine.core.managers.GameManager;
import org.cup.engine.core.nodes.GameNode;
import org.cup.engine.core.nodes.components.defaults.SpriteRenderer;

import java.io.File;

public class TutorialBackground extends GameNode {
    public TutorialBackground(){
        transform.setPosition(GameManager.game.getWindowDimentions().divide(2));
        transform.setScale(GameManager.game.getWindowDimentions());

        addChild(new SpriteRenderer(PathHelper.sprites + "tutorial" + File.separator + "background.png", transform, 0));
    }
}
