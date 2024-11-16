package org.cup.assets.objects;

import org.cup.assets.PathHelper;
import org.cup.engine.Vector;
import org.cup.engine.core.managers.GameManager;
import org.cup.engine.core.nodes.GameNode;
import org.cup.engine.core.nodes.components.Renderer;
import org.cup.engine.core.nodes.components.defaults.Animation;
import org.cup.engine.core.nodes.components.defaults.Animator;

import java.io.File;

public class Employee extends GameNode {
    private Animator animator = new Animator(transform, 2);
    private float speed = 100;
    private float additionalSpeed = 75;

    // State Machine
    public static final int IDLE = 0;
    public static final int TAKE_RESOURCE = 1;
    public static final int DELIVER_RESOURCE = 2;

    private int status = IDLE;

    private Machine roomMachine;
    private DropZone packageDropZone;

    private boolean isWaitingWithResource = false;

    private int employeeNumber = 0;

    public Employee(Room room, int n) {
        employeeNumber = n;

        String spritesFolder = PathHelper.sprites + "employee" + File.separator;

        Animation idle = new Animation(PathHelper.getFilePaths(spritesFolder + "idle"));
        idle.setLoopType(Animation.PING_PONG_LOOP);

        Animation idlePackage = new Animation(PathHelper.getFilePaths(spritesFolder + "idle-package"));
        idle.setLoopType(Animation.PING_PONG_LOOP);

        Animation walk = new Animation(PathHelper.getFilePaths(spritesFolder + "walk"), 125, true);
        walk.setLoopType(Animation.PING_PONG_LOOP);

        Animation walkPackage = new Animation(PathHelper.getFilePaths(spritesFolder + "walk-package"), 125, true);
        walkPackage.setLoopType(Animation.PING_PONG_LOOP);

        animator.addAnimation("idle", idle);
        animator.addAnimation("idle-package", idlePackage);
        animator.addAnimation("walk", walk);
        animator.addAnimation("walk-package", new Animation(PathHelper.getFilePaths(spritesFolder + "walk-package")));

        animator.setPivot(Renderer.BOTTOM_PIVOT);

        roomMachine = room.getMachine();

        packageDropZone = room.getDropZone();
    }

    @Override
    public void init() {
        addChild(animator);
        transform.setScale(new Vector(51, 58).multiply(1.5));
        transform.setPosition(new Vector(200, -5));
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
        double step = (speed + (UpgradeRoom.getBoostController().isEmployeeBoostActive() ? additionalSpeed : 0)) * GameManager.getDeltaTime();

        if (status == TAKE_RESOURCE) {
            if(!roomMachine.hasResource()){
                idle();
            }
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
        } else if (status == DELIVER_RESOURCE) {
            double stopPos = packageDropZone.transform.getPosition().x - transform.getScale().x / 2;
            if(packageDropZone.hasResource()){ // Wait a bit further before putting it in the drop zone
                stopPos -= 20 * (employeeNumber + 1);
            }
            if (pos.x < stopPos) {
                // Move Towards the machine
                transform.move(Vector.RIGHT.multiply(step));
            } else {
                // Drop the package
                if (packageDropZone.addResouce()) {
                    idle();
                } else {
                    if (packageDropZone.hasResource() && !isWaitingWithResource) {
                        animator.play("idle-package");
                        isWaitingWithResource = true;
                    }
                }

            }
            return;
        }

    }

    public void takeResource() {
        if (status != IDLE)
            return;
        if (transform.getScale().x > 0) {
            animator.flipHorizontally();
        }
        status = TAKE_RESOURCE;
        animator.play("walk");
    }

    private void deliverResource() {
        animator.play("walk-package");
        status = DELIVER_RESOURCE;
        animator.flipHorizontally();
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
