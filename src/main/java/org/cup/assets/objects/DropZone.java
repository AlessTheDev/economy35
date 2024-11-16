package org.cup.assets.objects;

import org.cup.assets.PathHelper;
import org.cup.assets.logic.DropZoneThread;
import org.cup.engine.Vector;
import org.cup.engine.core.nodes.GameNode;
import org.cup.engine.core.nodes.components.Renderer;
import org.cup.engine.core.nodes.components.defaults.Animation;
import org.cup.engine.core.nodes.components.defaults.Animator;

import java.io.File;

public class DropZone extends GameNode {
    private final Animator animator;

    private DropZoneThread thread;

    public DropZone(Vector position) {
        transform.setPosition(position);
        animator = new Animator(transform, 3);
    }

    public void init() {
        animator.setPivot(Renderer.RIGHT_PIVOT);
        transform.setScale(new Vector(25, 50));
        addChild(animator);

        initAnimator();

        thread = new DropZoneThread(this);
        thread.start();
    }

    private void initAnimator(){
        String baseSpritePath = PathHelper.sprites + "dropzone" + File.separator;

        Animation successAnimation = new Animation(PathHelper.getFilePaths(baseSpritePath + "success"));
        successAnimation.addLastFrameListener(() -> {
            animator.play("idle");
        });

        Animation fullAnimation = new Animation(PathHelper.getFilePaths(baseSpritePath + "fail"));

        animator.addAnimation("idle", new Animation(PathHelper.getFilePaths(baseSpritePath + "idle"), true)); 
        animator.addAnimation("success", successAnimation); 
        animator.addAnimation("full", fullAnimation); 
    }

    /**
     * Tries to place a resource
     * @return False if the drop zone is full, True if the resource has been placd
     */
    public boolean addResouce() {
        if (thread.placeResource()) { // Try to place the resource
            // If the resource has been placed set the sprite to full
            animator.play("full");
            return true;
        }
        return false; // The DropZone is already full
    }

    public void normalSprite() {
        animator.play("success");
    }

    public boolean hasResource(){
        return thread.hasResource();
    }
}
