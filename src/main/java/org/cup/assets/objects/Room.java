package org.cup.assets.objects;

import java.awt.Color;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.JPanel;

import org.cup.assets.PathHelper;
import org.cup.assets.UI.Floor;
import org.cup.assets.UI.GameButton;
import org.cup.assets.logic.Economy;
import org.cup.assets.objects.Elevator.ElevatorListener;
import org.cup.assets.objects.Machine.MachineUpgrade;
import org.cup.engine.Vector;
import org.cup.engine.core.Debug;
import org.cup.engine.core.nodes.components.Renderer;
import org.cup.engine.core.nodes.components.defaults.SpriteRenderer;
import org.cup.engine.core.nodes.components.defaults.Transform;

public class Room extends Floor implements ElevatorListener {
    private Machine machine = new Machine();
    private DropZone dropZone;

    private int floorId;

    // An array of the employee with a fixed number of employees (3)
    private Employee[] employees = new Employee[3];

    // The number of the employee
    private int nEmployees;
    private double newEmployeeCost = 20;

    private MachineUpgrade nextMachineUpgrade;

    // UI
    private JPanel buttonsPanel = new JPanel();
    GameButton upgradeMachineBtn = new GameButton(
            "<html><center>" + "UPGRADE MACHINE" + "<br>" + "(???)" + "</center></html>");

    GameButton hireEmployeeBtn = new GameButton(
            "<html><center>" + "HIRE EMPLOYEE" + "<br>($" + newEmployeeCost + ")</center></html>");

    /**
     * Constructor for the Room. Initializes the room with the specified parameters.
     *
     * @param width      The width of the room.
     * @param height     The height of the room.
     * @param x          The x-coordinate of the room's position.
     * @param y          The y-coordinate of the room's position.
     * @param layer      The layer of the room (used for rendering).
     * @param spritePath The path to the room's sprite.
     */
    public Room(int floorId, int width, int height, int x, int y, int layer, String spritePath) {
        this.floorId = floorId;

        // Set the position and scale of the room's transform.
        transform.setScale(Vector.ONE);
        transform.setPosition(new Vector(x, y));

        // Create and add the sprite renderer to the room.
        Transform rendererTransform = new Transform();
        rendererTransform.setScale(new Vector(width, height));
        SpriteRenderer sr = new SpriteRenderer(spritePath, rendererTransform, layer);
        sr.setPivot(Renderer.BOTTOM_LEFT_PIVOT);
        addChild(sr);
        rendererTransform.setParentTransform(transform);

        // Create and add the drop zone to the room.
        dropZone = new DropZone(new Vector(width, -30));
        dropZone.transform.setParentTransform(transform);
        addChild(dropZone);

        // Add the tube
        Transform tubeTransform = new Transform();
        tubeTransform.setPosition(new Vector(width, 0));
        tubeTransform.setScale(new Vector(40, 175));
        tubeTransform.setParentTransform(transform);

        SpriteRenderer tubeRenderer = new SpriteRenderer(PathHelper.sprites + "building" + File.separator + "Pipe.png", tubeTransform, 5);
        tubeRenderer.setPivot(Renderer.BOTTOM_LEFT_PIVOT);
        addChild(tubeRenderer);

        // Add Elevator Zone
        addChild(new ElevatorZone(transform));

        // Add the machine to the room.
        addChild(machine);
        machine.transform.setParentTransform(transform);

        // Hire the first employee.
        addEmployee();

        // Initialize the UI buttons and set up listeners.
        initUI();
    }

    @Override
    public void init() {
        Building.get().getElevator().addListener(this);
    }

    /**
     * Adds a new employee to the room, if possible.
     * Increases the number of employees in the room.
     */
    public void addEmployee() {
        if (nEmployees >= 3) {
            Debug.warn("MAX EMPLOYEES REACHED");
            return;
        }

        Employee e = new Employee(this, nEmployees);
        employees[nEmployees] = e;
        nEmployees++;

        // Add the new employee to the room.
        addChild(e);
        e.transform.setParentTransform(transform);
    }

    public Machine getMachine() {
        return machine;
    }

    public DropZone getDropZone() {
        return dropZone;
    }

    public void onUpdate() {
        // Update UI
        upgradeMachineBtn.setEnabled(nextMachineUpgrade != null && Economy.getBalance() >= nextMachineUpgrade.cost);
        hireEmployeeBtn.setEnabled(nEmployees < employees.length && Economy.getBalance() >= newEmployeeCost);
    }

    /**
     * Initializes the UI elements for the room, including buttons for upgrading the
     * machine
     * and hiring new employees.
     */
    private void initUI() {
        // Set up the layout for the buttons panel.
        buttonsPanel.setLayout(new GridLayout(1, 2, 10, 0));

        // Add buttons for upgrading the machine and hiring employees.
        buttonsPanel.add(upgradeMachineBtn);
        buttonsPanel.add(hireEmployeeBtn);

        // Set the colors for the "Hire Employee" button.
        hireEmployeeBtn.setColors(new Color(50, 192, 37), new Color(37, 160, 26));

        // Update the upgrade machine button with the correct cost and benefits.
        updateMachineUpgradeBtn();

        // Add action listeners for the buttons.
        upgradeMachineBtn.addActionListener(e -> upgradeMachine());
        hireEmployeeBtn.addActionListener(e -> hireEmployee());
    }

    /**
     * Updates the text and behavior of the "Upgrade Machine" button based on the
     * current
     * and next available upgrades for the machine.
     */
    private void updateMachineUpgradeBtn() {
        nextMachineUpgrade = machine.getNextUpgrade();
        MachineUpgrade currentUpgrade = machine.getCurrentUpgrade();

        // Calculate the increases in success rate and speed.
        double successRateIncrease = calculateSuccessRateIncrease(currentUpgrade, nextMachineUpgrade);
        double doublespeedIncrease = calculateDoublespeedIncrease(currentUpgrade, nextMachineUpgrade);

        // Update the button's text to reflect the next upgrade.
        upgradeMachineBtn
                .setText(formatUpgradeButtonText(nextMachineUpgrade, successRateIncrease, doublespeedIncrease));
    }

    private double calculateSuccessRateIncrease(MachineUpgrade current, MachineUpgrade next) {
        return next.probability - current.probability;
    }

    private double calculateDoublespeedIncrease(MachineUpgrade current, MachineUpgrade next) {
        return (current.interval - next.interval) / 1000;
    }

    /**
     * Formats the text for the upgrade machine button, displaying the cost and any
     * benefits.
     *
     * @param nextMachineUpgrade  The next machine upgrade.
     * @param successRateIncrease The increase in success rate.
     * @param doublespeedIncrease The increase in speed.
     * @return A formatted string for the button text.
     */
    private String formatUpgradeButtonText(MachineUpgrade nextMachineUpgrade, double successRateIncrease,
            double doublespeedIncrease) {
        return "<html><center>" +
                "UPGRADE MACHINE<br>($" + nextMachineUpgrade.cost + ")" +
                (successRateIncrease != 0 ? ("<br>+" + successRateIncrease + " SUCCESS RATE") : "") +
                (doublespeedIncrease != 0 ? ("<br>+" + doublespeedIncrease + " SPEED") : "") +
                "</center></html>";
    }

    /**
     * Handles the action of upgrading the machine. If the machine can be upgraded,
     * it performs the upgrade
     * and updates the button text accordingly.
     */
    private void upgradeMachine() {
        if (machine.canUpgrade()) {
            machine.upgrade();
            if (!machine.canUpgrade()) {
                nextMachineUpgrade = null;
                upgradeMachineBtn.setText("<html><center>MAX LEVEL</center></html>");
                upgradeMachineBtn.setEnabled(false);
            } else {
                updateMachineUpgradeBtn();
            }
        }
    }

    /**
     * Handles the action of hiring a new employee. If the player can afford to hire
     * an employee,
     * a new employee is added to the room.
     */
    private void hireEmployee() {
        if (Economy.getBalance() >= newEmployeeCost) {
            addEmployee();
            Economy.spendMoney(newEmployeeCost);
            if (nEmployees >= 3) {
                hireEmployeeBtn.setText("<html><center>MAX EMPLOYEES REACHED</center></html>");
            }
        }
    }

    @Override
    public JPanel getUI() {
        // Disable the upgrade machine button by default.
        upgradeMachineBtn.setEnabled(false);
        return buttonsPanel;
    }

    @Override
    public void onFloorChange(int floor) {
        if (floor == floorId) {
            machine.showUI();
        } else {
            machine.hideUI();
        }
    }
}
