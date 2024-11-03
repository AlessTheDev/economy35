package org.cup.assets.objects;

import org.cup.assets.PathHelper;
import org.cup.engine.Vector;
import org.cup.engine.core.managers.GameManager;
import org.cup.engine.core.nodes.GameNode;
import org.cup.engine.core.nodes.components.Renderer;
import org.cup.engine.core.nodes.components.defaults.Animation;
import org.cup.engine.core.nodes.components.defaults.Animator;

public class Employee extends GameNode {
    private Animator animator = new Animator(transform, 2);
    private String spritesFolder = PathHelper.sprites + "employee\\";
    private float speed = 100;

    // State Machine
    public static final int IDLE = 0;
    public static final int TAKE_RESOURCE = 1;
    public static final int DELIVER_RESOURCE = 2;

    private int status = IDLE;

    private Machine roomMachine;
    private DropZone packageDropZone;

    private boolean isWaitingWithResource = false;

    public Employee(Room room) {
        animator.addAnimation("idle", new Animation(PathHelper.getFilePaths(spritesFolder + "idle")));
        animator.addAnimation("walk", new Animation(PathHelper.getFilePaths(spritesFolder + "walk")));
        animator.addAnimation("walk-package", new Animation(PathHelper.getFilePaths(spritesFolder + "walk-package")));

        animator.setPivot(Renderer.BOTTOM_PIVOT);

        roomMachine = room.getMachine();

        packageDropZone = room.getDropZone();
    }

    @Override
    public void init() {
        addChild(animator);
        transform.setScale(new Vector(51, 58));
        transform.setPosition(new Vector(200, 0));
    }

    @Override
    public void onUpdate() {
        if (status == IDLE) {
            if (roomMachine.hasResource()) {
                takeResource();
            }
            return;
        }

        Vector pos = transform.getPosition();
        double step = speed * GameManager.getDeltaTime();

        if (status == TAKE_RESOURCE) {
            double machineOffset = roomMachine.transform.getScale().x - 50;
            if (pos.x > roomMachine.transform.getPosition().x + machineOffset) {
                // Move Towards the machine
                transform.move(Vector.LEFT.multiply(step));
            } else {
                // Take the package
                roomMachine.takeResource();
                deliverResource();
            }
            return;
        }

        if (status == DELIVER_RESOURCE) {
            if (pos.x < packageDropZone.transform.getPosition().x) {
                // Move Towards the machine
                transform.move(Vector.RIGHT.multiply(step));
            } else {
                // Drop the package
                if (packageDropZone.addResouce()) {
                    idle();
                } else{
                    if(packageDropZone.hasResource() && !isWaitingWithResource){
                        animator.play("idle");
                        isWaitingWithResource = true;
                    }
                }


            }
            return;
        }

    }

    public void flipCharacter() {
        transform.setScale(Vector.multiplyVec(new Vector(-1, 1), transform.getScale()));
    }

    public void takeResource() {
        if (status != IDLE)
            return;
        flipCharacter();
        status = TAKE_RESOURCE;
        animator.play("walk");
    }

    private void deliverResource() {
        animator.play("walk-package");
        status = DELIVER_RESOURCE;
        flipCharacter();
    }

    private void idle() {
        status = IDLE;
        animator.play("idle");
        isWaitingWithResource = false;
    }

    public int getStatus() {
        return status;
    }

}
