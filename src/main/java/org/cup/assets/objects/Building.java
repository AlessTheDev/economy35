package org.cup.assets.objects;

import org.cup.engine.Vector;
import org.cup.engine.core.Debug;
import org.cup.engine.core.nodes.GameNode;

import java.awt.Color;
import java.util.ArrayList;

public class Building extends GameNode {
    private static Building instance; // Singleton pattern

    private Inventory inventory = new Inventory(3);
    private Elevator elevator = new Elevator();

    private ArrayList<Room> rooms = new ArrayList<>();

    private int roomHeight = 175;
    int roomWidth = 500;

    public Building(){
        if(Building.instance != null) 
            Debug.err("More than one building has been initialized");
        Building.instance = this;
    }

    public static Building get(){
        return instance;
    }

    @Override
    public void init(){
        transform.setPosition(new Vector(120, 175*3));

        addChild(elevator);
        addChild(inventory);
    }

    public void addRoom(){
        int nRooms = rooms.size();

        Color col = nRooms % 2 == 0 ? new Color(221, 221, 221) : Color.GRAY;
        Room room = new Room(roomWidth, roomHeight, 0, (-roomHeight * nRooms), 1, col);

        room.transform.setParentTransform(transform);

        rooms.add(room);
        addChild(room);
    }

    public Inventory getInventory(){
        return inventory;
    }

    public Elevator getElevator(){
        return elevator;
    }

}
