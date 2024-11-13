package org.cup.assets.objects;

import org.cup.assets.PathHelper;
import org.cup.assets.UI.GameLabel;
import org.cup.assets.scenes.MainScene;
import org.cup.engine.Vector;
import org.cup.engine.core.managers.GameManager;
import org.cup.engine.core.managers.sound.SoundManager;
import org.cup.engine.core.nodes.GameNode;
import org.cup.engine.core.nodes.Scene;
import org.cup.engine.core.nodes.components.Renderer;
import org.cup.engine.core.nodes.components.defaults.SpriteRenderer;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JLabel;

public class TutorialSlides extends GameNode implements KeyListener {
    private MainScene mainScene = new MainScene();

    private class Slide {
        public String image;
        public String text;

        public Slide(String image, String text) {
            this.image = image;
            this.text = text;
        }
    }

    private String slidesPath = PathHelper.sprites + "\\tutorial\\";

    private Slide[] slides = new Slide[] {
            new Slide(slidesPath + "1.png", "Welcome, new entrepreneur, to “economy 35”, your new company"),
            new Slide(slidesPath + "2.png", "Your goal here is to sell as much products as possible"),
            new Slide(slidesPath + "3.png", "With the money earned you'll be able to upgrade your company"),
            new Slide(slidesPath + "4.png", "This will help you increase your income and make your company bigger"),
            new Slide(slidesPath + "5.png", "Of course, you'll have to keep an eye on your company's stat"),
            new Slide(slidesPath + "6.png", "I believe you'll be a great entrepreneur"),
            new Slide(slidesPath + "7.png", "Oh, and remember to save some money for..."),
            new Slide(slidesPath + "8.png", "TAXES… good luck")
    };

    private SpriteRenderer sr;
    private int currentSlide;

    private GameLabel textLabel;

    public TutorialSlides() {
        transform.setScale(GameManager.game.getWindowDimentions().multiply(0.4));
        transform.setPosition(
                GameManager.game.getWindowDimentions().divide(2).add(new Vector(0, -transform.getScale().y)));

        textLabel = new GameLabel("<html><center>TEST</html></center>");
        // Set the alignment to center horizontally and vertically
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setVerticalAlignment(JLabel.CENTER);
        textLabel.setBounds(transform.getPosition().getX() - transform.getScale().getX() / 2,
                transform.getPosition().getY() + transform.getScale().getY(), transform.getScale().getX(), 100);

        sr = new SpriteRenderer(slides[0].image, transform, 1);
        sr.setPivot(Renderer.TOP_PIVOT);
        addChild(sr);

        GameManager.game.addKeyListener(this);
    }

    @Override
    public void init() {
        GameManager.game.addUIElement(textLabel);
        SoundManager.playClip(MainScene.getTaxesMusic());
    }

    private void nextSlide() {
        currentSlide++;
        if (currentSlide == slides.length) {
            SoundManager.stopClip(MainScene.getTaxesMusic());

            GameManager.game.addScene(mainScene);
            Scene tutorialScene = ((Scene) getParent());
            tutorialScene.disable();
            GameManager.game.removeScene(tutorialScene);
            return;
        }
        loadSlide(slides[currentSlide]);
    }

    private void loadSlide(Slide s) {
        sr.setSprite(s.image);
        textLabel.setText("<html><center>" + s.text + "</html></center>");
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        nextSlide();
    }

    @Override
    public void onDisable() {
        GameManager.game.removeUIElement(textLabel);
        GameManager.game.removeKeyListener(this);
    }
}