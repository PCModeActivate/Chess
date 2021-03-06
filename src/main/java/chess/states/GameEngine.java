package main.java.chess.states;

import main.java.chess.util.DualColours;
import processing.core.PApplet;

public class GameEngine {
    private static GameEngine instance;
    public PApplet parent;
    public boolean running = true;
    GameState gameState;

    public static GameEngine getInstance() {
        if (instance == null) instance = new GameEngine();
        return instance;
    }
    
    public void start() {
        running = true;
    }
    
    public void end() {
    }

    public void changeState(GameState newState) {
        if (gameState != null) gameState.end();
        parent.background(DualColours.getText());
        gameState = newState;
        gameState.start();
    }

    public void update() {
        gameState.update();
    }

    public void draw() {
        gameState.draw();
    }

    public void exit() {
        end();
        running = false;
    }
}
