package org.cup.assets.objects;

import org.cup.assets.PathHelper;
import org.cup.engine.Vector;
import org.cup.engine.core.Debug;
import org.cup.engine.core.managers.GameManager;
import org.cup.engine.core.nodes.GameNode;
import org.cup.engine.core.nodes.components.Renderer;
import org.cup.engine.core.nodes.components.defaults.Animation;
import org.cup.engine.core.nodes.components.defaults.Animator;

public class Player extends GameNode {
    private Animator animator = new Animator(transform, 5);
    private String spritesFolder = PathHelper.sprites + "player\\";
    
    @Override
    public void init() {
        addChild(animator);
        animator.addAnimation("idle", new Animation(PathHelper.getFilePaths(spritesFolder + "idle")));
        animator.setPivot(Renderer.BOTTOM_PIVOT);
        transform.setScale(new Vector(105, 134));
        transform.setPosition(new Vector(60, 0));
    }

    @Override
    public void onEnable() {
        Debug.log("Player Enabled");
        animator.play("idle");
    }

    @Override
    public void onUpdate() {
        // This will be executed every frame
        // Debug.log(transform.getParentTransform().getPosition());
    }

    @Override
    public void onDisable() {

    }


}

