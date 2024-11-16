package org.cup.assets.objects;

import java.util.Random;
import java.io.File;

import org.cup.assets.PathHelper;
import org.cup.engine.Vector;
import org.cup.engine.core.managers.GameManager;
import org.cup.engine.core.nodes.GameNode;
import org.cup.engine.core.nodes.components.Renderer;
import org.cup.engine.core.nodes.components.defaults.Animation;
import org.cup.engine.core.nodes.components.defaults.Animator;
import org.cup.engine.core.nodes.components.defaults.Transform;
import org.cup.assets.logic.CustomerThread;

public class Customer extends GameNode {
    private Animator animator = new Animator(transform, 3);
    private float speed = 100;

    private int positionInQueue;

    // State Machine
    private int status = TAKE_RESOURCE;
    public static final int TAKE_RESOURCE = 0;
    public static final int WAITING_FOR_RESOURCE = 1;
    public static final int GO_AWAY = 2;

    private CustomerThread thread;

    private Transform seller;

    private boolean isFancy;

    private Random randomGen = new Random();

    public Customer() {
        isFancy = false;

        String spritesFolder = PathHelper.sprites + "customer" + File.separator;

        Animation walk = new Animation(PathHelper.getFilePaths(spritesFolder + "walk"));
        Animation walkPackage = new Animation(PathHelper.getFilePaths(spritesFolder + "walk-package"));

        Animation walkFancy = new Animation(PathHelper.getFilePaths(spritesFolder + "walk-fancy"));
        Animation walkPackageFancy = new Animation(PathHelper.getFilePaths(spritesFolder + "walk-package-fancy"));

        walk.setLoopType(Animation.PING_PONG_LOOP);
        walkPackage.setLoopType(Animation.PING_PONG_LOOP);

        walkFancy.setLoopType(Animation.PING_PONG_LOOP);
        walkPackageFancy.setLoopType(Animation.PING_PONG_LOOP);

        animator.addAnimation("idle", new Animation(PathHelper.getFilePaths(spritesFolder + "idle")));
        animator.addAnimation("walk", walk);
        animator.addAnimation("walk-package", walkPackage);

        animator.addAnimation("walk-fancy", walkFancy);
        animator.addAnimation("walk-package-fancy", walkPackageFancy);
        animator.addAnimation("idle-fancy", new Animation(PathHelper.getFilePaths(spritesFolder + "idle-fancy")));

        animator.setPivot(Renderer.BOTTOM_PIVOT);

        thread = new CustomerThread(this, Building.get().getInventory());
    }

    @Override
    public void init() {
        addChild(animator);
        transform.setScale(new Vector(51, 58).multiply(2));
        transform.setPosition(new Vector(200, 0));
    }

    @Override
    public void onEnable() {
        transform.setPosition(GameManager.game.getWindowDimentions().add(new Vector(10, -15)));
        animator.setLayer(2);
        isFancy = randomGen.nextInt(100) < 8;

        takeResource();


        seller = Building.get().getMarket().transform;
    }

    @Override
    public void onUpdate() {
        double step = speed * GameManager.getDeltaTime();

        Vector pos = transform.getPosition();

        if (status == WAITING_FOR_RESOURCE || status == TAKE_RESOURCE) {
            double waitingPos = seller.getPosition().x + 70;
            waitingPos += Math.abs(transform.getScale().x) / 3 * positionInQueue;
            if (pos.x > waitingPos) {
                // Move Towards the store
                transform.move(Vector.LEFT.multiply(step));
            }

            if (status == TAKE_RESOURCE) {
                if (pos.x <= waitingPos) {
                    playAnimation("idle");
                    status = WAITING_FOR_RESOURCE;
                    thread.takePackage(isFancy ? 3 : 1);
                }
                return;
            }
        }

        if (status == GO_AWAY) {
            if (pos.x < GameManager.game.getWidth() + transform.getScale().x / 2) {
                // Move Towards the machine
                transform.move(Vector.RIGHT.multiply(step));
            } else {
                disable();
            }
            return;
        }

    }

    public void takeResource() {
        Building.get().getMarket().joinQueue(this);

        status = TAKE_RESOURCE;
        playAnimation("walk");
    }

    public void goAway() {
        Building.get().getMarket().playSellAnimation();

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        status = GO_AWAY;
        playAnimation("walk-package"); 
        animator.setLayer(4);
        // transform.setPosition(transform.getPosition().subtract(Vector.DOWN.multiply(15)));

        Building.get().getMarket().moveQueue(positionInQueue);
    }

    @Override
    public void onDisable() {
        ((CustomerSpawner) getParent()).addBackToQueue(this);
    }

    public void setPositionInQueue(int value) {
        positionInQueue = value;
    }

    private void playAnimation(String status){
        animator.play(status + (isFancy ? "-fancy" : ""));
    }
}
