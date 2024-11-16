package org.cup.assets.objects;

import java.awt.Color;
import java.util.ArrayList;
import java.io.File;

import org.cup.assets.PathHelper;
import org.cup.assets.UI.GameLabel;
import org.cup.assets.scenes.MainScene;
import org.cup.engine.Utils;
import org.cup.engine.core.Debug;
import org.cup.engine.core.managers.GameManager;
import org.cup.engine.core.managers.sound.SoundManager;
import org.cup.engine.core.nodes.GameNode;
import org.cup.engine.core.nodes.components.Renderer;
import org.cup.engine.core.nodes.components.defaults.Animation;
import org.cup.engine.core.nodes.components.defaults.Animator;

public class DayCycle extends GameNode {
    private static int currentHour;

    public interface DayListener {
        void onDayEnd();

        void onDayResume();
    }

    private ArrayList<DayListener> listeners = new ArrayList<>();

    private double minutesInRealLife = 1; // One minute irl
    private double minutesInGame = 480 * 2; // One minute irl = 60 min in game (480 is usually ok)
    private double timeInMinutesGame = 0; // Keeps track of in-game minutes

    // TaxTime music

    Animator animator = new Animator(transform, -10);

    private GameLabel timeLabel;

    private boolean isNight;
    private boolean taxGuyCutscene;
    private boolean isFirstDay;

    private boolean isPlayingTaxMusic;

    public DayCycle() {
        transform.setScale(GameManager.game.getWindowDimentions());

        isNight = false;
        taxGuyCutscene = false;
        isFirstDay = true;
        isPlayingTaxMusic = false;

        // Animator
        String[] sprites = PathHelper.getFilePaths(PathHelper.sprites + "day-night-cycle" + File.separator);
        Animation dayToNight = new Animation(sprites, false);

        Utils.reverseArray(sprites);
        Animation nightToDay = new Animation(sprites, false);

        animator.addAnimation("night-to-day", nightToDay);
        animator.addAnimation("day-to-night", dayToNight);

        addChild(animator);
        animator.setPivot(Renderer.TOP_LEFT_PIVOT);

        
    }

    @Override
    public void init() {
        // UI
        timeLabel = new GameLabel("Time: 0h");
        timeLabel.setFontSize(20);
        timeLabel.setBounds(10, 10, 240, 20);
        GameManager.game.addUIElement(timeLabel);
        timeLabel.setForeground(new Color(150, 150, 150));
    }

    @Override
    public void onUpdate() {
        // Calculate how many minutes in the game should elapse based on delta time
        double deltaTimeInMinutes = GameManager.getDeltaTime() / 60.0;
        double gameMinutesElapsed = deltaTimeInMinutes * (minutesInGame / minutesInRealLife);

        if (!taxGuyCutscene) {
            timeInMinutesGame += gameMinutesElapsed;
        }

        timeInMinutesGame = timeInMinutesGame % (24 * 60);

        // Calculate the in-game hour
        currentHour = (int) (timeInMinutesGame / 60);

        timeLabel.setText("Time: " + currentHour + "h");

        if (!isNight && currentHour == 20) {
            animator.play("day-to-night");
            timeLabel.setForeground(Color.WHITE);
            MainScene.getStatsPanel().nightMode();
            isNight = true;
            isFirstDay = false;
        }

        if (isNight && currentHour == 7) {
            animator.play("night-to-day");
            timeLabel.setForeground(new Color(150, 150, 150));
            MainScene.getStatsPanel().dayMode();
            isNight = false;
        }

        if (!isPlayingTaxMusic && currentHour == 22) {
            SoundManager.stopClip(MainScene.getMusic());

            isPlayingTaxMusic = true;
        }

        if (!taxGuyCutscene && currentHour == 0) {
            if (isFirstDay)
                return;
            taxGuyCutscene = true;
            notifyDayEndListeners();

            new Thread(() -> {
                // Start cutscene
                MainScene.taxText.enable();
                MainScene.taxText.show();

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Debug.err(e.getMessage());
                }

                SoundManager.setVolume(MainScene.getTaxesMusic(), 1);
                SoundManager.playClip(MainScene.getTaxesMusic());

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Debug.err(e.getMessage());
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Debug.err(e.getMessage());
                }

                MainScene.taxGuy.enable();
                MainScene.taxGuy.show();
            }).start();

        }
    }

    public void exitCutscene() {
        timeInMinutesGame += 60;
        taxGuyCutscene = false;
        isPlayingTaxMusic = false;
        SoundManager.stopClip(MainScene.getTaxesMusic());
        SoundManager.setVolume(MainScene.getMusic(), 1);
        SoundManager.playClip(MainScene.getMusic());
        notifyDayResumeListeners();
    }

    public void addListener(DayListener listener) {
        listeners.add(listener);
    }

    private void notifyDayEndListeners() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onDayEnd();
        }
    }

    private void notifyDayResumeListeners() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onDayResume();
        }
    }

    public int getCurrentHour(){
        return currentHour;
    }

}
