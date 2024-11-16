package org.cup.assets.objects;

import java.util.Random;
import java.io.File;

import org.cup.assets.PathHelper;
import org.cup.assets.scenes.MainScene;
import org.cup.engine.Vector;
import org.cup.engine.core.nodes.GameNode;

public class BoostController extends GameNode {
    private int customerBoost = 0;
    private int previousHour = 0;

    private Particle[] employeeBoostParticles = new Particle[24]; 

    private UpgradeRoom upgradeRoom;

    private Random randomGen = new Random();


    public BoostController(UpgradeRoom upgradeRoom){
        this.upgradeRoom = upgradeRoom;
        for (int i = 0; i < employeeBoostParticles.length; i++) {
            employeeBoostParticles[i] = new Particle(PathHelper.sprites + "upgrades" + File.separator + "star.png", new Vector(randomGen.nextDouble(90)), 5);
            employeeBoostParticles[i].setCollisionBox(upgradeRoom.getCollisionBox());
        }
    }

    @Override
    public void onEnable() {
        previousHour = MainScene.dayCycle.getCurrentHour();
    }

    @Override
    public void onUpdate() {
        int h = MainScene.dayCycle.getCurrentHour();

        if(h != previousHour){
            if(isEmployeeBoostActive()){
                customerBoost--;
                Particle toRemove = employeeBoostParticles[customerBoost];
                toRemove.disable();
                upgradeRoom.removeChild(toRemove);
            }
            previousHour = h;
        }
    }

    public boolean isEmployeeBoostActive(){
        return customerBoost > 0;
    }

    public void enableEmployeeBoostUpgrade(){
        customerBoost = 24;
        for(Particle p : employeeBoostParticles){
            upgradeRoom.addChild(p);
            p.transform.setScale(new Vector(20));
            p.transform.setPosition(new Vector(randomGen.nextInt(Building.ROOM_WIDTH), randomGen.nextInt(Building.ROOM_HEIGHT)));
        }
    }
}
