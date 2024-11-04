package org.cup.engine.core.managers.graphics;

import org.cup.engine.core.managers.RenderingQueue;
import org.cup.engine.core.nodes.components.Renderer;

import javax.swing.*;
import java.awt.*;

/**
 * The {@code Painter} class is a custom JPanel that handles the rendering of
 * all components in the rendering queue. It supports features like scaling
 * and antialiasing.
 */
public class Painter extends JPanel {
    protected RenderingQueue queue;

    /**
     * Antialiasing is a technique used in computer graphics to remove the aliasing effect.
     * The aliasing effect is the appearance of jagged edges in a rasterized image (an image rendered using pixels).
     * The problem of jagged edges technically occurs due to distortion
     * of the image when scan conversion is done with sampling at a low
     * frequency, which is also known as Undersampling. Aliasing occurs when real-world
     * objects which comprise smooth, continuous curves are rasterized using pixels.
     * The cause of anti-aliasing is Undersampling.
     * Enabling antialiasing may impact performance.
     */
    private boolean antialiasing;
    private float scale;

    // #region Getters & Setters
    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public boolean isAntialiasingEnabled() {
        return antialiasing;
    }

    public void setAntialiasing(boolean antialiasing) {
        this.antialiasing = antialiasing;
    }
    // #endregion

    public Painter() {
        queue = new RenderingQueue();
        this.setLayout(null);
    }

    /**
     * Paints the components on the JPanel.
     * This method is called whenever the component needs to be redrawn.
     *
     * @param g The Graphics context for painting.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // Render each component in the queue.
        int size = queue.size();

        g2.scale(scale, scale);
        if (antialiasing) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        setDoubleBuffered(true);

        for (int i = 0; i < size; i++) {
            Renderer component = queue.get(i);
            if (component != null)
                component.render(g2);
        }
    }
}

