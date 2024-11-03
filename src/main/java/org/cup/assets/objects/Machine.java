package org.cup.assets.objects;

import org.cup.assets.PathHelper;
import org.cup.engine.Vector;
import org.cup.engine.core.nodes.GameNode;
import org.cup.engine.core.nodes.components.Renderer;
import org.cup.engine.core.nodes.components.defaults.Animation;
import org.cup.engine.core.nodes.components.defaults.Animator;
import org.cup.engine.core.nodes.components.defaults.SpriteRenderer;

import java.util.Random;

public class Machine extends GameNode {
    private final Animator animator = new Animator(transform, 1);

    private Random randomGen = new Random();

    private int probability = 40; // Probability to drop the resource

    private double lastAttempt; // The time passed since the last time it has been chec
    private double interval = 5000; // Time of the interval(in milliseconds)

    private boolean hasProducedResource;

    @Override
    public void init() {
        addChild(animator);
        animator.setPivot(Renderer.BOTTOM_LEFT_PIVOT);

        addAnimationsToAnimator();

        transform.setScale(new Vector(100));
    }

    private void addAnimationsToAnimator(){

        String spritesFolder = PathHelper.sprites + "machine\\1\\";

        Animation failAnimation = new Animation(PathHelper.getFilePaths(spritesFolder + "fail"), false);
        failAnimation.addLastFrameListener(() -> {
            animator.play("loading");
        });

        Animation packageOutAnimation = new Animation(PathHelper.getFilePaths(spritesFolder + "success\\package-out"), false);
        packageOutAnimation.addLastFrameListener(() -> {
            animator.play("success-idle");
        });

        animator.addAnimation("fail", failAnimation);
        animator.addAnimation("loading", new Animation(PathHelper.getFilePaths(spritesFolder + "loading")));
        animator.addAnimation("success-package-out", packageOutAnimation);
        animator.addAnimation("success-idle", new Animation(PathHelper.getFilePaths(spritesFolder + "success\\idle")));
    }

    @Override
    public void onUpdate() {
        if (!hasResource() && System.currentTimeMillis() - lastAttempt > interval) {
            attemptToCreateResource();
            lastAttempt = System.currentTimeMillis();
        }
    }

    public void error() {
        animator.play("fail");
    }

    public void success() {
        animator.play("success-package-out");
        hasProducedResource = true;
    }

    public void attemptToCreateResource() {
        // Drop the resoure randomly based on the value of the probability, the higher
        // it gets the more probability there is to get a resource
        if (randomGen.nextInt(100) <= probability) {
            success();
        } else {
            error();
        }
    }

    // return if the machine ha a resource
    public boolean hasResource() {
        return hasProducedResource;
    }

    // Reset the machine taking away its resource
    public void takeResource() {
        animator.play("loading");
        hasProducedResource = false;
        lastAttempt = System.currentTimeMillis();
    }
}
