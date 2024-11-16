package org.cup.assets.objects;

import org.cup.assets.PathHelper;
import org.cup.assets.UI.Floor;
import org.cup.engine.Vector;
import org.cup.engine.core.Debug;
import org.cup.engine.core.nodes.GameNode;

import java.io.File;
import java.util.ArrayList;

public class Building extends GameNode {
    // Singleton instance of the Building class
    private static Building instance;

    // Core building components
    private UpgradeRoom upgradeRoom;
    private Inventory inventory;
    private Elevator elevator;
    private Market market;

    // Collection of floors in the building, including rooms and the inventory floor
    private ArrayList<Floor> floors;
    private NewFloor floorUnderConstruction;

    public static final int ROOM_WIDTH = 500;
    public static final int ROOM_HEIGHT = 175;

    public Building() {
        if (instance != null) {
            Debug.err("More than one building has been initialized");
        }

        instance = this;
        upgradeRoom = new UpgradeRoom();
        inventory = new Inventory(3);
        elevator = new Elevator();
        market = new Market();
        floors = new ArrayList<>();

        floors.add(inventory);
        floors.add(upgradeRoom);
        addChild(upgradeRoom); // Adds the upgrade room as a child node
        
        
        floorUnderConstruction = new NewFloor(ROOM_WIDTH, ROOM_HEIGHT, 1);
        floors.add(floorUnderConstruction);
        addChild(floorUnderConstruction);
    }

    /**
     * Returns the singleton instance of Building.
     *
     * @return The single Building instance.
     */
    public static Building get() {
        return instance;
    }

    @Override
    public void init() {
        super.init();

        addChild(elevator); // Adds the elevator as a child node
        addChild(inventory); // Adds the inventory as a child node
        addChild(market); // Adds the market as a child node

        // Sets the parent transform for inventory and market to match Building's
        // transform
        upgradeRoom.transform.setParentTransform(transform);
        inventory.transform.setParentTransform(transform);
        market.transform.setParentTransform(transform);
    }

    public void addRoom() {
        floors.remove(floorUnderConstruction);

        int nRooms = floors.size() - 1;  // Ignore inventory

        String basePath = PathHelper.sprites + "building" + File.separator + "rooms" + File.separator + "room-background";
        String finalPath = basePath + (nRooms % 2 == 0 ? "-decorated" : "") + ".png";

        Vector newRoomPos = getNewFloorPos(nRooms);
        Room room = new Room(nRooms + 1, ROOM_WIDTH, ROOM_HEIGHT, newRoomPos.getX(), newRoomPos.getY(), 1, finalPath);

        room.transform.setParentTransform(transform);
        floors.add(room);
        addChild(room);

        Vector underConstructionPos = getNewFloorPos(nRooms + 1);
        floorUnderConstruction.transform.setPosition(underConstructionPos);
        floors.add(floorUnderConstruction);
    }

    private Vector getNewFloorPos(int nFloors){
        return new Vector(120 - 3, 175 * 3 + (-ROOM_HEIGHT * nFloors));
    }

    // #region Getters
    public Inventory getInventory() {
        return inventory;
    }

    public Elevator getElevator() {
        return elevator;
    }

    public Market getMarket() {
        return market;
    }

    /**
     * Retrieves the floor above the specified current floor.
     *
     * @param currentFloor The index of the current floor.
     * @return The floor above the current floor, or null if there is none.
     */
    public Floor getUpFloor(int currentFloor) {
        if (currentFloor + 1 >= floors.size()) {
            return null;
        }
        return floors.get(currentFloor + 1);
    }

    /**
     * Retrieves the floor below the specified current floor.
     *
     * @param currentFloor The index of the current floor.
     * @return The floor below the current floor, or null if there is none.
     */
    public Floor getDownFloor(int currentFloor) {
        if (currentFloor - 1 < 0) {
            return null;
        }
        return floors.get(currentFloor - 1);
    }
    // #endregion
}
