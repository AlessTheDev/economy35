package org.cup.engine.core.nodes.components.defaults;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.cup.engine.Vector;
import org.cup.engine.core.Debug;
import org.cup.engine.core.managers.ResourceManager;

/**
 * Represents an animation consisting of multiple frames (sprites).
 */
public class Animation {
    public interface AnimationLastFrameListener {
        public void onLastFrame();        
    }

    private final String[] spritePaths;
    private float timeBetweenFrames;
    private final boolean loop;
    private int currentFrameIndex;
    private Vector currentScale = Vector.ONE;
    private ArrayList<AnimationLastFrameListener> lastFrameListeners;
    

    /**
     * Constructs an Animation with the specified frames, timing, and looping
     * behavior.
     *
     * @param sprites           Array of image file paths for the animation frames.
     * @param timeBetweenFrames Time in milliseconds to wait between frames.
     * @param loop              Whether the animation should loop.
     */
    public Animation(String[] sprites, float timeBetweenFrames, boolean loop) {
        this.spritePaths = sprites;
        this.timeBetweenFrames = timeBetweenFrames;
        this.currentFrameIndex = 0;
        this.loop = loop;

        lastFrameListeners = new ArrayList<>();
        
        if (sprites.length == 0) {
            Debug.engineLogErr("The animation is empty");
        }
    }

    public Animation(String[] sprites, boolean loop) {
        this(sprites, 100, loop);
    }

    public Animation(String[] sprites) {
        this(sprites, true);
    }

     /**
     * Retrieves the current frame of the animation at the specified scale.
     * 
     * @param scale Vector containing the desired width (X) and height (Y) for the frame
     * @return The current frame as a scaled Image, or null if the current frame index is out of bounds
     */
    public Image getCurrentFrame(Vector scale) {
        if (currentFrameIndex >= spritePaths.length) {
            return null;
        }
        
        return ResourceManager.getImage(
            spritePaths[currentFrameIndex],
            scale.getX(),
            scale.getY()
        );
    }
    
    /**
     * Updates the current scale of the animation if it differs from the provided scale.
     * This method is used to optimize scaling operations by avoiding unnecessary updates.
     *
     * @param newScale The new scale vector to apply to the animation
     */
    public void updateScale(Vector newScale) {
        if (!newScale.equals(currentScale)) {
            currentScale = newScale;
        }
    }
    
    /**
     * Advances to and returns the next frame of the animation at the specified scale.
     * If the animation is set to loop and the last frame is reached, it will restart
     * from the first frame.
     *
     * @param scale Vector containing the desired width (X) and height (Y) for the frame
     * @return The next frame as a scaled Image, or null if there are no more frames
     */
    public Image nextFrame(Vector scale) {
        updateScale(scale);
        
        if (isLastFrame()) {
            notifyLastFrameListeners();
            if(loop){
                currentFrameIndex = 0;
            }
        }
        
        Image frame = getCurrentFrame(scale);
        currentFrameIndex++;
        return frame;
    }

    /**
     * Checks if the current frame is the last frame of the animation.
     *
     * @return True if the current frame is the last; otherwise false
     */
    public boolean isLastFrame() {
        return currentFrameIndex >= spritePaths.length;
    }
    /**
     * Resets the animation to the first frame.
     */
    public void reset() {
        currentFrameIndex = 0;
    }

    /**
     * Gets the time between frames in milliseconds.
     *
     * @return The time between frames.
     */
    public float getTimeBetweenFrames() {
        return timeBetweenFrames;
    }

    /**
     * Sets the time between frames.
     *
     * @param speed The new time between frames in milliseconds.
     */
    public void setTimeBetweenFrames(float speed) {
        this.timeBetweenFrames = speed;
    }

    /**
     * Checks if the animation loops.
     *
     * @return True if the animation loops; otherwise false.
     */
    public boolean isLoop() {
        return loop;
    }

    public void addLastFrameListener(AnimationLastFrameListener listener){
        lastFrameListeners.add(listener);
    }

    private void notifyLastFrameListeners(){
        for(int i = 0; i < lastFrameListeners.size(); i++){
            lastFrameListeners.get(i).onLastFrame();
        }
    }
}
