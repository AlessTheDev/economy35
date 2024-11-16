package org.cup.assets.objects;

import java.awt.Color;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.JPanel;

import org.cup.assets.PathHelper;
import org.cup.assets.UI.Floor;
import org.cup.assets.UI.GameButton;
import org.cup.assets.scenes.MainScene;
import org.cup.engine.Vector;
import org.cup.engine.core.Debug;
import org.cup.engine.core.nodes.components.Renderer;
import org.cup.engine.core.nodes.components.defaults.SpriteRenderer;
import org.cup.engine.core.nodes.components.defaults.Transform;
import org.cup.assets.logic.Economy;

/* 
 * Graphical part of the inventory
*/
public class Inventory extends Floor {
    private SpriteRenderer inventoryUI;
    private int buffer; // Packages in the inventory
    private int maxSize; // Maximum storage capacity
    private int increaseInventoryCost = 50;
    private int increaseProductValueCost = 80;

    private static final String SPRITE_BASE_PATH = PathHelper.sprites + "building" + File.separator + "stock" + File.separator;

    // UI
    private JPanel buttonsPanel = new JPanel();

    GameButton increaseInventoryBtn = new GameButton(
            "<html><center>" + "INCREASE INVENTORY" + "<br>($" + increaseInventoryCost + ")</center></html>");
    GameButton increaseProductValueBtn = new GameButton(
            "<html><center>" + "INCREASE PRODUCT VALUE" + "<br>($" + increaseProductValueCost + ")</center></html>");

    public Inventory(int initialMaxSize) {
        buffer = 0;
        maxSize = initialMaxSize;

        inventoryUI = new SpriteRenderer(SPRITE_BASE_PATH + "0.png", transform, 1);
        updateInventoryStatsUI();

        initUI();
    }

    public void init() {
        int w = 400;
        int h = 175;
        int y = 720 - 20 - 175 + h;
        int x = 120 - 1;

        transform.setScale(new Vector(w, h));
        transform.setPosition(new Vector(x, y));
        inventoryUI.setPivot(Renderer.BOTTOM_LEFT_PIVOT);

        Transform elevatorZoneTransform = new Transform();
        elevatorZoneTransform.setPosition(new Vector(x, y));
        addChild(new ElevatorZone(elevatorZoneTransform));

        addChild(inventoryUI);

        updateProductValueButtonText();
    }

    @Override
    public void onUpdate() {
        increaseInventoryBtn.setEnabled(Economy.getBalance() >= increaseInventoryCost);
        increaseProductValueBtn.setEnabled(Economy.getBalance() >= increaseProductValueCost
                && Building.get().getMarket().getNextUpgrade() != null);
    }

    /**
     * Adds a single resource to the inventory buffer.
     * This method blocks if the inventory is full.
     */
    public synchronized void addResource() {
        while (buffer >= maxSize) {
            try {
                wait(); // Wait until space becomes available
            } catch (Exception e) {
                Debug.err(e.getMessage());
            }
        }
        buffer++; // Increment the inventory buffer
        updateInventoryStatsUI(); // Update UI to reflect new inventory count
        notifyAll(); // Notify any waiting threads
    }

    /**
     * Removes a specified number of resources from the inventory buffer.
     * This method blocks if the inventory doesn't contain enough resources.
     *
     * @param n The number of resources to remove.
     */
    public synchronized void takeResource(int n) {
        while (buffer < n) {
            try {
                wait(); // Wait until enough resources are available
            } catch (Exception e) {
                Debug.engineLogErr(e.getMessage());
            }
        }
        Economy.setBalance(Economy.getBalance() + Economy.getProductValue() * n); // update UI counters

        buffer -= n; // Decrement the inventory buffer
        updateInventoryStatsUI(); // Update UI to reflect new inventory count
        notifyAll(); // Notify any waiting threads
    }

    /**
     * Gets the current count of resources in the inventory.
     *
     * @return The current resource count.
     */
    public int getResourceCount() {
        return buffer;
    }

    /**
     * Gets the maximum capacity of the inventory.
     *
     * @return The maximum capacity.
     */
    public int getCapacity() {
        return maxSize;
    }

    /**
     * Updates the inventory stats display in the UI, reflecting
     * changes in resource count or capacity.
     */
    private void updateInventoryStatsUI() {
        updateSprite();
        MainScene.getStatsPanel().setInventoryLabel(this);
    }

    /**
     * Initializes the UI, setting up buttons for adding floors
     * and increasing inventory capacity with appropriate event listeners.
     */
    private void initUI() {
        buttonsPanel.setLayout(new GridLayout(1, 2, 10, 0));

        increaseInventoryBtn.setColors(
                new Color(50, 192, 37),
                new Color(37, 160, 26));

        increaseInventoryBtn.addActionListener(e -> {
            Economy.spendMoney(increaseInventoryCost);
            maxSize++;
            MainScene.getStatsPanel().setInventoryLabel(this);
        });

        increaseProductValueBtn.addActionListener(e -> {
            Building.get().getMarket().upgradeLevel();
            Economy.spendMoney(increaseProductValueCost);
            updateProductValueButtonText();
        });
        // Add buttons to the panel
        buttonsPanel.add(increaseInventoryBtn);
        buttonsPanel.add(increaseProductValueBtn);
    }

    private void updateProductValueButtonText() {
        Integer valueIncrease = Building.get().getMarket().getNextUpgrade();

        if (valueIncrease != null) {
            increaseProductValueBtn.setText("<html><center>" + "INCREASE PRODUCT VALUE by " + valueIncrease + "<br>($"
                    + increaseProductValueCost + ")</center></html>");
        } else {
            increaseProductValueBtn.setText("<html><center>MAX</center></html>");
        }
    }

    @Override
    public JPanel getUI() {
        return buttonsPanel;
    }

    /**
     * Updates the sprite based on the inventory capacity.
     */
    public void updateSprite() {
        float percentage = buffer / (float) maxSize;

        String spriteSuffix = "0.png";

        if (percentage >= 1f) {
            spriteSuffix = "100.png"; // Full capacity
        } else if (percentage >= 0.75f) {
            spriteSuffix = "75.png"; // 75% capacity
        } else if (percentage >= 0.50f) {
            spriteSuffix = "50.png"; // 50% capacity
        } else if (percentage >= 0.25f) {
            spriteSuffix = "25.png"; // 25% capacity
        }

        inventoryUI.setSprite(SPRITE_BASE_PATH + spriteSuffix);
    }

}