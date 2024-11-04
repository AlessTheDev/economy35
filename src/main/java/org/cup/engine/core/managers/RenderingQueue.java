package org.cup.engine.core.managers;

import org.cup.engine.core.Debug;
import org.cup.engine.core.nodes.components.Renderer;

import java.util.ArrayList;

/**
 * The {@code RenderingQueue} manages the ordered list of {@code Renderer} objects
 * in the game. Renderers are drawn in the order determined by their layers.
 * <p>
 * The queue maintains layer order upon adding or updating a renderer.
 * </p>
 */
public class RenderingQueue {
    private ArrayList<Renderer> renderers;

    public RenderingQueue() {
        renderers = new ArrayList<Renderer>();
    }

    /**
     * Adds a {@code Renderer} to the queue while maintaining the correct order
     * based on the renderer's layer.
     *
     * @param obj The {@code Renderer} to add.
     */
    public synchronized void addObject(Renderer obj) {
        // "put it under the carpet" type solution
        if (renderers.contains(obj)) // ? why is this even a problem anyways 
            return;

        int index = findInsertionIndex(obj.getLayer());
        renderers.add(index, obj); // Insert at the correct position
    }

    /**
     * Removes a {@code Renderer} from the queue.
     *
     * @param obj The {@code Renderer} to remove.
     */
    public synchronized void removeObject(Renderer obj) {
        while (renderers.contains(obj)) {
            renderers.remove(obj);
        }
    }

    /**
     * Updates a {@code Renderer}'s layer by removing it from the queue and
     * re-adding it to maintain the correct layer order.
     *
     * @param obj The {@code Renderer} whose layer has changed.
     */
    public void updateObject(Renderer obj) {
        // Remove the object from the current list
        renderers.remove(obj);
        // Add it back at the correct position
        addObject(obj);
    }

    public void printRenderQueue() {
        for (Renderer obj : renderers) {
            Debug.engineLog(obj.toString());
        }
    }

    /**
     * Helper method to find the correct insertion index for a renderer based
     * on its layer, using binary search for efficiency.
     *
     * @param layer The layer of the renderer to insert.
     * @return The index at which to insert the renderer.
     */
    private int findInsertionIndex(int layer) {
        int left = 0;
        int right = renderers.size();

        while (left < right) {
            int mid = (left + right) / 2;
            if (renderers.get(mid).getLayer() <= layer) {
                left = mid + 1; // Go to the right half
            } else {
                right = mid; // Stay in the left half
            }
        }
        return left; // The correct index to insert
    }

    public Renderer get(int i){
        return renderers.get(i);
    }

    public int size(){
        return renderers.size();
    }
}

