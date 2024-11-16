package org.cup.assets.objects;

import java.io.File;
import java.awt.GridLayout;
import javax.swing.JPanel;

import org.cup.assets.PathHelper;
import org.cup.assets.UI.GameButton;
import org.cup.assets.UI.Floor;
import org.cup.assets.components.CollisionBox;
import org.cup.assets.logic.Economy;
import org.cup.engine.Vector;
import org.cup.engine.core.managers.GameManager;
import org.cup.engine.core.nodes.components.Renderer;
import org.cup.engine.core.nodes.components.defaults.Animation;
import org.cup.engine.core.nodes.components.defaults.Animator;
import org.cup.engine.core.nodes.components.defaults.SpriteRenderer;
import org.cup.engine.core.nodes.components.defaults.Transform;

// the damaged room on the first floor 
class UpgradeRoom extends Floor {
    private Animator animator;
    private CollisionBox collisionBox;

    static BoostController boostController;

    // UI
    private JPanel buttonsPanel;
    private GameButton unlockButton;
    private GameButton boostButton;

    // boosts
    private int unlockCost = 400;
    private int boostCost = 90;

    public UpgradeRoom() {
        transform.setScale(Vector.ONE);
        transform.setPosition(new Vector(116, GameManager.game.getHeight() - 59 - Building.ROOM_HEIGHT));

        // Create and add the sprite renderer to the room.
        Transform rendererTransform = new Transform();
        rendererTransform.setScale(new Vector(Building.ROOM_WIDTH, Building.ROOM_HEIGHT));

        Animation locked = new Animation(
                new String[] { PathHelper.getSpritePath(File.separator + "building"+ File.separator + "rooms"+ File.separator + "room-empty.png") }, false);
        Animation idle = new Animation(PathHelper.getFilePaths(PathHelper.sprites + File.separator + "building"+ File.separator + "rooms"+ File.separator + "boost-room"));

        animator = new Animator(rendererTransform, 1);
        animator.setPivot(Renderer.BOTTOM_LEFT_PIVOT);
        addChild(animator);
        rendererTransform.setParentTransform(transform);

        animator.addAnimation("locked", locked);
        animator.addAnimation("idle", idle);

        // new Vector(0, -rendererTransform.getScale().y)
        Vector scale = rendererTransform.getScale();
        collisionBox = new CollisionBox(new Vector(scale.x / 2, -scale.y / 2), rendererTransform.getScale());
        collisionBox.transform.setParentTransform(transform);

        // Add the tube
        Transform tubeTransform = new Transform();
        tubeTransform.setPosition(new Vector(Building.ROOM_WIDTH, 0));
        tubeTransform.setScale(new Vector(41, 175));
        tubeTransform.setParentTransform(transform);
        SpriteRenderer tubeRenderer = new SpriteRenderer(PathHelper.sprites + "building" + File.separator + "Pipe.png", tubeTransform, 5);
        tubeRenderer.setPivot(Renderer.BOTTOM_LEFT_PIVOT);
        addChild(tubeRenderer);

        // Add Elevator Zone
        addChild(new ElevatorZone(transform));

        boostController = new BoostController(this);
        addChild(boostController);

        // UI
        initUI();

    }

    /**
     * Initializes the UI elements for the room,
     * including buttons for boosting production
     */
    private void initUI() {
        buttonsPanel = new JPanel();

        // Set up the layout for the buttons panel.
        buttonsPanel.setLayout(new GridLayout(1, 1, 10, 0));

        boostButton = new GameButton(
                "<html><center>" + "BOOST EMPLOYEES" + "<br>" + "($" + boostCost + ")" + "</center></html>");
        unlockButton = new GameButton(
                "<html><center>" + "UNLOCK" + "<br>" + "($" + unlockCost + ")" + "</center></html>");
        buttonsPanel.add(unlockButton);

        // Update the upgrade machine button with the correct cost and benefits.
        // updateMachineUpgradeBtn();

        // Add action listeners for the buttons.
        unlockButton.addActionListener(e -> unlockUpgradeRoom());
        boostButton.addActionListener(e -> boostEmployees());
    }

    public void onUpdate() {
        // Update UI
        unlockButton.setEnabled(Economy.getBalance() >= unlockCost);
        boostButton.setEnabled(Economy.getBalance() >= boostCost);
    }

    private void unlockUpgradeRoom() {
        Economy.spendMoney(unlockCost);
        buttonsPanel.remove(unlockButton);
        buttonsPanel.add(boostButton);

        animator.play("idle");
    }

    private void boostEmployees() {
        if (Economy.getBalance() >= boostCost) {
            Economy.spendMoney(boostCost);
            boostController.enableEmployeeBoostUpgrade();
        }
    }

    public JPanel getUI() {
        return buttonsPanel;
    };

    public CollisionBox getCollisionBox() {
        return collisionBox;
    }

    public static BoostController getBoostController() {
        return boostController;
    }

}
