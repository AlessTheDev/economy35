package org.cup.assets.objects;

import java.util.ArrayList;
import java.io.File;

import javax.sound.sampled.Clip;

import org.cup.assets.PathHelper;
import org.cup.assets.logic.Economy;
import org.cup.engine.Vector;
import org.cup.engine.core.Debug;
import org.cup.engine.core.managers.GameManager;
import org.cup.engine.core.managers.sound.SoundManager;
import org.cup.engine.core.nodes.GameNode;
import org.cup.engine.core.nodes.components.Renderer;
import org.cup.engine.core.nodes.components.defaults.Animation;
import org.cup.engine.core.nodes.components.defaults.Animator;

public class Market extends GameNode {
    private Animator animator = new Animator(transform, 2);

    private ArrayList<Customer> customersQueue = new ArrayList<>();

    private int[] productValues = {
        10, // Level 1
        20, // Level 2
        30, // Level 3
        40, // Level 4
    };

    private int level;

    // SFX
    private Clip cashOut = SoundManager.createClip(PathHelper.SFX + "Cash.wav", false);

    public Market() {
        transform.setPosition(new Vector(630, GameManager.game.getHeight() - 59));
        transform.setScale(new Vector(240, 175));
        level = 0;

        initAnimator();
    }

    private void initAnimator() {
        animator.setPivot(Renderer.BOTTOM_PIVOT);
        addChild(animator);
    }

    @Override
    public void init() {
        addAnimations(0);        
        addAnimations(1);        
    }

    private void addAnimations(int level){
        // Define the sprites folder path for the animations
        String spritesFolder = PathHelper.sprites + "shop" + File.separator + level + File.separator;

        // Create a sell animation that transitions back to idle after it finishes
        Animation sellAnimation = new Animation(PathHelper.getFilePaths(spritesFolder + "sell"), false);
        sellAnimation.addLastFrameListener(() -> {
            animator.play("idle-" + level); // Play idle animation after sell animation
        });

        // Create an idle animation with a specified frame delay
        Animation idleAnimation = new Animation(PathHelper.getFilePaths(spritesFolder + "idle"));
        idleAnimation.setTimeBetweenFrames(200); // Adjust the time between frames to slow down the idle animation

        // Add the animations to the animator
        animator.addAnimation("idle-" + level, idleAnimation);
        animator.addAnimation("sell-" + level, sellAnimation);
    }

    /**
     * Adds a customer to the market's queue and sets their position in the queue.
     * 
     * @param c The customer to add
     */
    public void joinQueue(Customer c) {
        c.setPositionInQueue(customersQueue.size()); // Set the customer's queue position based on the current size
        customersQueue.add(c); // Add the customer to the queue
    }

    /**
     * Moves the customers in the queue after one is served.
     * 
     * @param servedIndex The index of the served customer to be removed
     */
    public synchronized void moveQueue(int servedIndex) {
        customersQueue.remove(servedIndex); // Remove the served customer from the queue
        // Update the position of each customer in the queue
        for (int i = 0; i < customersQueue.size(); i++) {
            customersQueue.get(i).setPositionInQueue(i); // Update their position based on their index
        }
    }

    public void playSellAnimation() {
        animator.play("sell-" + (level >= 2 ? 1 : 0));
        SoundManager.playClip(cashOut);
    }

    public void upgradeLevel(){
        if(getNextUpgrade() == null){
            Debug.warn("Can't upgrade PRODUCT VALUE");
        }
        level++;
        Economy.setProductValue(productValues[level]);
    }

    public Integer getNextUpgrade(){
        if(level + 1 == productValues.length){
            return null;
        }
        return productValues[level + 1];
    }
}
